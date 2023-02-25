# dxlab-backend-api-v2

# Getting Started

### Jasypt
Para cifrar claves desde la terminal de intellij debemos ejecutar el siguiente comando:
> mvn jasypt:encrypt-value "-Djasypt.encryptor.password=theKey" "-Djasypt.plugin.value=theWordToBeEncrypted"

Como resultado tenemos:
> **ENC(hGI2KCIarlo5eO/i/hE9nA==)**

Para descifrar claves desde la terminal de intellij debemos ejecutar el siguiente comando:
> mvn jasypt:decrypt-value -Djasypt.encryptor.password="the password" -Djasypt.plugin.value="DbG1GppXOsFa2G69PnmADvQFI3esceEhJYbaEIKCcEO5C85JEqGAhfcjFMGnoRFf"


Esto lo agregaremos en el archivo de propiedades del proyecto

### App
Desplegar el artefactor agregando esta propiedad
> -Djasypt.encryptor.password=u8x/A?D(G+KbPeShVmYq3s6v9y$B&E)H
