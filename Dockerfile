FROM payara/micro:5.2020.4-jdk11

MAINTAINER boris.heithecker "boris.heithecker@gmx.net"

#USER payara 

#ENV DOMAIN_DIR=${PAYARA_DIR}/glassfish/domains/${DOMAIN_NAME}

COPY --chown=payara:payara postgresql-42.2.16.jar ${PAYARA_HOME}/

COPY --chown=payara:payara target/sphairas-login-0.9-SNAPSHOT.war $DEPLOY_DIR/sphairas-login.war

COPY --chown=payara:payara bin/post-boot-commands.asadmin ${PAYARA_HOME}/

#COPY --chown=payara:payara bin/pre-boot-commands.asadmin bin/post-boot-commands.asadmin ${CONFIG_DIR}/
#COPY bin/pre-boot.sh ${SCRIPT_DIR}/init_0_pre-boot.sh

EXPOSE 8080 9009

#"--contextroot", "auth", 
CMD ["--deploymentDir", "/opt/payara/deployments", "--addLibs", "/opt/payara/postgresql-42.2.16.jar", "--postbootcommandfile", "/opt/payara/post-boot-commands.asadmin"]