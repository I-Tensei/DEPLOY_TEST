#!/bin/bash
# deploy.sh - Simple deployment script to run on bastion host

echo "=== DEPLOY_TEST Auto Deployment Started ==="

# Variables
JAR_URL="https://github.com/I-Tensei/DEPLOY_TEST/releases/latest/download/demo-0.0.1-SNAPSHOT.jar"
REPO_URL="https://github.com/I-Tensei/DEPLOY_TEST.git"
PRIVATE_SERVER="10.0.2.123"

# Function to deploy backend
deploy_backend() {
    echo "=== Backend Deployment ==="
    
    # Stop existing Java process on private server
    ssh -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
        ec2-user@${PRIVATE_SERVER} 'pkill java || true; sleep 3'
    
    # Build and deploy new JAR (if local repo exists)
    if [ -d "/tmp/DEPLOY_TEST" ]; then
        cd /tmp/DEPLOY_TEST
        git pull origin main
        cd backend/demo
        mvn clean package -DskipTests
        
        # Transfer new JAR to private server
        scp -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
            target/demo-0.0.1-SNAPSHOT.jar ec2-user@${PRIVATE_SERVER}:/home/ec2-user/demo-0.0.1-SNAPSHOT.jar
    else
        echo "Repository not found locally. Using existing JAR."
    fi
    
    # Start application on private server
    ssh -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
        ec2-user@${PRIVATE_SERVER} 'cd /home/ec2-user && nohup java -jar demo-0.0.1-SNAPSHOT.jar --server.address=0.0.0.0 > app.log 2>&1 & sleep 5'
    
    echo "✓ Backend deployment completed"
}

# Function to deploy frontend
deploy_frontend() {
    echo "=== Frontend Deployment ==="
    
    if [ -d "/tmp/DEPLOY_TEST" ]; then
        cd /tmp/DEPLOY_TEST/frontend
        npm ci
        npm run build
        
        # Deploy to nginx
        sudo rm -rf /usr/share/nginx/html/*
        sudo cp -r build/* /usr/share/nginx/html/
        sudo chown -R nginx:nginx /usr/share/nginx/html/
        sudo systemctl restart nginx
        
        echo "✓ Frontend deployment completed"
    else
        echo "Repository not found locally."
    fi
}

# Main deployment process
main() {
    # Clone or update repository
    if [ ! -d "/tmp/DEPLOY_TEST" ]; then
        cd /tmp
        git clone ${REPO_URL}
    fi
    
    deploy_backend
    deploy_frontend
    
    echo "=== Deployment completed successfully ==="
}

# Run main function
main "$@"
