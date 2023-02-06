create_directory() {
  local directory=$1
  local owner=$2
  local group=$3
  local mode=$4

  if [ ! -d "$directory" ]; then
    mkdir -p "$directory"
    chown "$owner":"$group" "$directory"
    chmod "$mode" "$directory"
  fi
}
Và gọi hàm create_directory trong một vòng lặp để tạo các thư mục:

bash
Copy code
directories=(
  ["/opt/abc/data", "tomcat", "tomcat", 0775]
  ["/opt/abc/logs", "tomcat", "tomcat", 0775]
  ["/opt/abc/conf", "tomcat", "tomcat", 0775]
)
for dir in "${directories[@]}"; do
  create_directory "${dir[0]}" "${dir[1]}" "${dir[2]}" "${dir[3]}"
done


Download the Tomcat Binary:
bash
Copy code
wget https://downloads.apache.org/tomcat/tomcat-9/v9.0.37/bin/apache-tomcat-9.0.37.tar.gz
Extract the Binary:
Copy code
tar -xzvf apache-tomcat-9.0.37.tar.gz
Move the Binary to the Opt Directory:
bash
Copy code
sudo mv apache-tomcat-9.0.37 /opt/tomcat
Create a Tomcat User:
Copy code
sudo useradd -r tomcat
Change the Ownership of the Tomcat Directory:
bash
Copy code
sudo chown -R tomcat:tomcat /opt/tomcat
Create a Systemd Unit File:
bash
Copy code
sudo nano /etc/systemd/system/tomcat.service
Add the Following Configuration:
javascript
Copy code
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking

User=tomcat
Group=tomcat

Environment=JAVA_HOME=/usr/lib/jvm/jre
Environment=CATALINA_PID=/opt/tomcat/temp/tomcat.pid
Environment=CATALINA_HOME=/opt/tomcat
Environment=CATALINA_BASE=/opt/tomcat
Environment='CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC'
Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

[Install]
WantedBy=multi-user.target
Reload the Systemd Daemon:
Copy code
sudo systemctl daemon-reload
Start the Tomcat Service:
sql
Copy code
sudo systemctl start tomcat
Enable the Tomcat Service:
bash
Copy code
sudo systemctl enable tomcat
Check the Status of the Tomcat Service:
lua
Copy code
sudo systemctl status tomcat
