package test;
#!/bin/bash

# Update the system
sudo yum update -y

# Install Java
sudo yum install java-1.8.0-openjdk -y

# Download Tomcat
wget http://apachemirror.wuchna.com/tomcat/tomcat-8/v8.5.56/bin/apache-tomcat-8.5.56.tar.gz

# Extract Tomcat
tar -xvf apache-tomcat-8.5.56.tar.gz

# Move Tomcat to /opt
sudo mv apache-tomcat-8.5.56 /opt/tomcat

# Create a symbolic link to make it easier to manage the Tomcat version
sudo ln -s /opt/tomcat/apache-tomcat-8.5.56 /opt/tomcat/latest

# Add environment variables for Tomcat
echo 'export CATALINA_HOME="/opt/tomcat/latest"' >> ~/.bashrc
source ~/.bashrc

# Install RPM
sudo yum install rpm -y
# Set up Tomcat as a service
sudo bash -c 'cat << EOF > /etc/systemd/system/tomcat.service
[Unit]
Description=Tomcat 8.5.56
After=syslog.target network.target

[Service]
Type=forking
User=tomcat
Group=tomcat
Environment=CATALINA_PID=$CATALINA_HOME/temp/tomcat.pid
Environment=CATALINA_HOME=$CATALINA_HOME
Environment=CATALINA_BASE=$CATALINA_HOME
ExecStart=$CATALINA_HOME/bin/startup.sh
ExecStop=$CATALINA_HOME/bin/shutdown.sh

[Install]
WantedBy=multi-user.target
EOF'

# Enable the Tomcat service
sudo systemctl enable tomcat

# Start the Tomcat service
sudo systemctl start tomcat


# Start the Tomcat service
sudo systemctl start tomcat

# Set up HTTPS
sudo yum install mod_ssl -y
sudo bash -c 'cat << EOF > /etc/httpd/conf.d/ssl.conf
Listen 443
<VirtualHost *:443>
  ServerName localhost
  SSLEngine on
  SSLCertificateFile /etc/pki/tls/certs/localhost.crt
  SSLCertificateKeyFile /etc/pki/tls/private/localhost.key
  <Proxy *>
    Order allow,deny
    Allow from all
  </Proxy>
  <Location />
    ProxyPass http://localhost:8080/
    ProxyPassReverse http://localhost:8080/
  </Location>
</VirtualHost>
EOF'

# Create the SSL certificate
sudo bash -c 'openssl req -x509 -newkey rsa:4096 -keyout /etc/pki/tls/private/localhost.key -out /etc/pki/tls/certs/localhost.crt -days 365 -nodes -subj "/CN=localhost"'

# Restart Apache to pick up the changes
sudo systemctl



import javafx.application.Application;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Admin on 30/05/2017.
 */
public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        HelloWorld helloWorld = (HelloWorld) context.getBean("helloWorld1");
        helloWorld.print();

    }
}
