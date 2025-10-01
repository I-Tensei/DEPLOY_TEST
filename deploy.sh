#!/bin/bash
# deploy.sh - Simple deployment script to run on bastion host

echo "=== DEPLOY_TEST Auto Deployment Started ==="

# Variables
JAR_URL="https://github.com/I-Tensei/DEPLOY_TEST/releases/latest/download/demo-0.0.1-SNAPSHOT.jar"
REPO_URL="https://github.com/I-Tensei/DEPLOY_TEST.git"
PRIVATE_SERVER="172.27.11.203"
# Fallback DB options: use in-memory H2 to ensure the app starts even if MySQL isn't ready
H2_DB_OPTS="--spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE --spring.datasource.driverClassName=org.h2.Driver --spring.datasource.username=sa --spring.datasource.password= --spring.jpa.hibernate.ddl-auto=update --spring.sql.init.mode=never --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"

# Function to deploy backend
deploy_backend() {
    echo "=== Backend Deployment ==="
    
    # Stop existing Java process on private server
    ssh -i ~/.ssh/0715.pem -o StrictHostKeyChecking=no \
        ec2-user@${PRIVATE_SERVER} 'pkill java || true; sleep 3'
    
    # Build and deploy new JAR (if local repo exists)
    if [ -d "/tmp/DEPLOY_TEST" ]; then
        cd /tmp/DEPLOY_TEST/backend/demo
        mvn clean package -DskipTests
        
        # Transfer new JAR to private server
        scp -i ~/.ssh/0715.pem -o StrictHostKeyChecking=no \
            target/demo-0.0.1-SNAPSHOT.jar ec2-user@${PRIVATE_SERVER}:/home/ec2-user/demo-0.0.1-SNAPSHOT.jar
    else
        echo "Repository not found locally. Using existing JAR."
    fi
    
    # Start application on private server
    # Start with H2 by default so the API comes up even without MySQL.
    # To use MySQL, set USE_H2=false in your environment and extend DB_OPTS as needed.
    if [ "${USE_H2}" = "false" ]; then
        # Build from MYSQL_* env if provided; otherwise use externally provided DB_OPTS
        MYSQL_HOST_OR_DEFAULT=${MYSQL_HOST:-}
        MYSQL_PORT_OR_DEFAULT=${MYSQL_PORT:-3306}
        MYSQL_DB_OR_DEFAULT=${MYSQL_DB:-}
        MYSQL_USER_OR_DEFAULT=${MYSQL_USER:-}
        MYSQL_PASSWORD_OR_DEFAULT=${MYSQL_PASSWORD:-}
        if [ -n "${MYSQL_HOST_OR_DEFAULT}" ] && [ -n "${MYSQL_DB_OR_DEFAULT}" ] && [ -n "${MYSQL_USER_OR_DEFAULT}" ]; then
            DB_OPTS="--spring.datasource.url=jdbc:mysql://${MYSQL_HOST_OR_DEFAULT}:${MYSQL_PORT_OR_DEFAULT}/${MYSQL_DB_OR_DEFAULT}?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Tokyo --spring.datasource.username=${MYSQL_USER_OR_DEFAULT} --spring.datasource.password=${MYSQL_PASSWORD_OR_DEFAULT} --spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect --spring.sql.init.mode=never"
        else
            DB_OPTS="${DB_OPTS}"  # allow caller to pass MySQL options via DB_OPTS
        fi
    else
        DB_OPTS="${H2_DB_OPTS}"
    fi

    # Start application on private server with MySQL (USE_H2=false), detect Java path
        ssh -i ~/.ssh/0715.pem -o StrictHostKeyChecking=no \
        ec2-user@${PRIVATE_SERVER} "cd /home/ec2-user && . ~/.bash_profile && \
                nohup java -jar demo-0.0.1-SNAPSHOT.jar --server.address=0.0.0.0 --spring.profiles.active=mysql > app.log 2>&1 & sleep 5 && tail -n 50 app.log | sed -n '1,120p' || true"

    # Seed MySQL with schema.sql and data.sql when using MySQL (ensure files are in ~/ schema.sql / data.sql on bastion)
    if [ "${USE_H2}" = "false" ]; then
        echo "Seeding MySQL with schema.sql and data.sql..."
        if [ -f "~/schema.sql" ] && [ -f "~/data.sql" ]; then
            scp -i /home/ec2-user/0715.pem -o StrictHostKeyChecking=no \
                    ~/schema.sql \
                    ~/data.sql \
                    ec2-user@${PRIVATE_SERVER}:/home/ec2-user/
            ssh -i /home/ec2-user/0715.pem -o StrictHostKeyChecking=no \
                ec2-user@${PRIVATE_SERVER} "export MYSQL_PWD='${MYSQL_PASSWORD:-}'; mysql < /home/ec2-user/schema.sql && mysql < /home/ec2-user/data.sql && mysql -N -e 'SELECT COUNT(*) FROM items'" || {
                echo "MySQL seeding failed. Please check credentials or app.log.";
            }
        else
            echo "schema.sql or data.sql not found in home directory on bastion. Skipping seeding."
        fi
    fi
    
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
    # Offline environment: skip git clone/pull
    # if [ ! -d "/tmp/DEPLOY_TEST" ]; then
    #     cd /tmp
    #     git clone ${REPO_URL}
    # fi
    
    deploy_backend
    deploy_frontend
    
    echo "=== Deployment completed successfully ==="
}

# Run main function
main "$@"
