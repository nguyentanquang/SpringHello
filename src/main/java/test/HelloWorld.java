import os
import urllib.request
# Download Tomcat installation package
url = 'https://downloads.apache.org/tomcat/tomcat-9/v9.0.55/bin/apache-tomcat-9.0.55.tar.gz'
filename = url.split('/')[-1]
urllib.request.urlretrieve(url, filename)

# Extract Tomcat installation package
with tarfile.open(filename, 'r:gz') as tar:
    tar.extractall()

# Move extracted directory to /usr/local
shutil.move(f'apache-tomcat-9.0.55', '/usr/local/tomcat')
