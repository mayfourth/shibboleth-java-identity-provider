@echo off

rem This is a rudimentary script to generate credentials for the IdP.
rem Run from within the idp.home directory as bin\creds.bat.

set HOSTNAME=idp.example.org

set URI_ALT_NAME=https://idp.example.org/idp/shibboleth

set CLASS=net.shibboleth.utilities.java.support.security.SelfSignedCertificateGenerator

java -cp "bin\lib\*;war\WEB-INF\lib\*" %CLASS% --hostname %HOSTNAME% --keyfile creds/idp-signing.key --certfile creds/idp-signing.crt --uriAltName %URI_ALT_NAME%

java -cp "bin\lib\*;war\WEB-INF\lib\*" %CLASS% --hostname %HOSTNAME% --keyfile creds/idp-encryption.key --certfile creds/idp-encryption.crt --uriAltName %URI_ALT_NAME%

java -cp "bin\lib\*;war\WEB-INF\lib\*" %CLASS% --hostname %HOSTNAME% --storefile creds/idp-tls.p12 --storepass changeit --uriAltName %URI_ALT_NAME%

keytool -genseckey -alias idpSecretKey -keypass password -storepass password -storetype JCEKS -keyalg AES -keysize 256 -keystore creds/sealer.jks