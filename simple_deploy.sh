#!/bin/bash
# simple_deploy.sh - Ultra simple deployment script

echo "=== Simple Deployment Started ==="

# Backend deployment
echo "Deploying backend..."
ssh -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
    ec2-user@172.27.11.203 'pkill java || true; sleep 3'

# Copy JAR if exists
if [ -f "/tmp/demo-app.jar" ]; then
    scp -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
        /tmp/demo-app.jar ec2-user@172.27.11.203:/home/ec2-user/demo-0.0.1-SNAPSHOT.jar
fi

# Start application
# Start with H2 fallback unless USE_H2=false is provided
# You can switch to MySQL by setting USE_H2=false and providing MYSQL_* env vars.
H2_DB_OPTS="--spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE --spring.datasource.driverClassName=org.h2.Driver --spring.datasource.username=sa --spring.datasource.password= --spring.jpa.hibernate.ddl-auto=update --spring.sql.init.mode=never --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"

# Build MySQL options from env if provided
MYSQL_HOST_OR_DEFAULT=${MYSQL_HOST:-}
MYSQL_PORT_OR_DEFAULT=${MYSQL_PORT:-3306}
MYSQL_DB_OR_DEFAULT=${MYSQL_DB:-}
MYSQL_USER_OR_DEFAULT=${MYSQL_USER:-}
MYSQL_PASSWORD_OR_DEFAULT=${MYSQL_PASSWORD:-}

if [ "$USE_H2" = "false" ]; then
    if [ -n "$MYSQL_HOST_OR_DEFAULT" ] && [ -n "$MYSQL_DB_OR_DEFAULT" ] && [ -n "$MYSQL_USER_OR_DEFAULT" ]; then
        DB_OPTS="--spring.datasource.url=jdbc:mysql://${MYSQL_HOST_OR_DEFAULT}:${MYSQL_PORT_OR_DEFAULT}/${MYSQL_DB_OR_DEFAULT}?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Tokyo --spring.datasource.username=${MYSQL_USER_OR_DEFAULT} --spring.datasource.password=${MYSQL_PASSWORD_OR_DEFAULT} --spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect --spring.sql.init.mode=never"
    else
        # Fallback to user-supplied DB_OPTS if provided externally
        DB_OPTS="${DB_OPTS}"
    fi
else
    DB_OPTS="$H2_DB_OPTS"
fi

ssh -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
    ec2-user@172.27.11.203 "cd /home/ec2-user && nohup java -jar demo-0.0.1-SNAPSHOT.jar --server.address=0.0.0.0 ${DB_OPTS} > app.log 2>&1 & sleep 5 && tail -n 50 app.log | sed -n '1,120p' || true"

# Frontend deployment
echo "Deploying frontend..."
if [ -d "/tmp/frontend" ]; then
    sudo rm -rf /usr/share/nginx/html/*
    sudo cp -r /tmp/frontend/* /usr/share/nginx/html/
    sudo chown -R nginx:nginx /usr/share/nginx/html/
    sudo systemctl restart nginx
fi

# Optional: Seed MySQL with schema.sql and data.sql
if [ "$USE_H2" = "false" ] && [ "$SEED_DATA" = "true" ]; then
    echo "Seeding MySQL with schema.sql and data.sql..."
    if [ -f "/tmp/DEPLOY_TEST/backend/demo/src/main/resources/schema.sql" ] && [ -f "/tmp/DEPLOY_TEST/backend/demo/src/main/resources/data.sql" ]; then
        scp -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
            /tmp/DEPLOY_TEST/backend/demo/src/main/resources/schema.sql \
            /tmp/DEPLOY_TEST/backend/demo/src/main/resources/data.sql \
            ec2-user@172.27.11.203:/home/ec2-user/

        MYSQL_HOST_OR_DEFAULT=${MYSQL_HOST:-localhost}
        MYSQL_PORT_OR_DEFAULT=${MYSQL_PORT:-3306}
        MYSQL_DB_OR_DEFAULT=${MYSQL_DB:-demo_db}
        MYSQL_USER_OR_DEFAULT=${MYSQL_USER:-demo_user}
        MYSQL_PASSWORD_OR_DEFAULT=${MYSQL_PASSWORD:-}

        ssh -i /home/ec2-user/MyKeyPair.pem -o StrictHostKeyChecking=no \
            ec2-user@172.27.11.203 "export MYSQL_PWD='${MYSQL_PASSWORD_OR_DEFAULT}'; mysql -h ${MYSQL_HOST_OR_DEFAULT} -P ${MYSQL_PORT_OR_DEFAULT} -u ${MYSQL_USER_OR_DEFAULT} ${MYSQL_DB_OR_DEFAULT} < /home/ec2-user/schema.sql && mysql -h ${MYSQL_HOST_OR_DEFAULT} -P ${MYSQL_PORT_OR_DEFAULT} -u ${MYSQL_USER_OR_DEFAULT} ${MYSQL_DB_OR_DEFAULT} < /home/ec2-user/data.sql && mysql -h ${MYSQL_HOST_OR_DEFAULT} -P ${MYSQL_PORT_OR_DEFAULT} -u ${MYSQL_USER_OR_DEFAULT} -N -e 'SELECT COUNT(*) FROM items' ${MYSQL_DB_OR_DEFAULT}; unset MYSQL_PWD" || echo "MySQL seeding failed"
    else
        echo "schema.sql or data.sql not found under /tmp/DEPLOY_TEST. Skipping seeding."
    fi
fi

echo "✓ Deployment completed"
