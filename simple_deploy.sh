#!/bin/bash
# simple_deploy.sh - Ultra simple deployment script

echo "=== Simple Deployment Started ==="

# Backend deployment
echo "Deploying backend..."
ssh -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
    ec2-user@10.0.2.123 'pkill java || true; sleep 3'

# Copy JAR if exists
if [ -f "/tmp/demo-app.jar" ]; then
    scp -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
        /tmp/demo-app.jar ec2-user@10.0.2.123:/home/ec2-user/demo-0.0.1-SNAPSHOT.jar
fi

# Start application
ssh -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
    ec2-user@10.0.2.123 'cd /home/ec2-user && nohup java -jar demo-0.0.1-SNAPSHOT.jar --server.address=0.0.0.0 > app.log 2>&1 & sleep 5'

# Frontend deployment
echo "Deploying frontend..."
if [ -d "/tmp/frontend" ]; then
    sudo rm -rf /usr/share/nginx/html/*
    sudo cp -r /tmp/frontend/* /usr/share/nginx/html/
    sudo chown -R nginx:nginx /usr/share/nginx/html/
    sudo systemctl restart nginx
fi

echo "✓ Deployment completed"
