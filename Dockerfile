FROM tomcat:9

# Copy files from local disk to image
COPY Jupiter.war /usr/local/tomcat/webapps/
