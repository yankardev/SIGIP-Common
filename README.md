# SIGIP Common

Librería Java compartida por los microservicios de SIGIP.

## Contenido previsto

- Manejo estándar de errores HTTP.
- Excepciones comunes de negocio y disponibilidad.
- Contrato de eventos de pedidos.
- Componentes compartidos de seguridad JWT.
- Propagación del encabezado Authorization en llamadas OpenFeign.

## Requisitos

- Java 21 o superior.
- Maven 3.9+ (o Maven Wrapper).
- Spring Boot 3.5.6 y Spring Cloud 2025.0.0.

## Compilar

```powershell
mvn clean verify
```

## Consumo desde otros microservicios

Los servicios que utilicen esta librería deben declarar la dependencia `pe.sigip:sigip-common:0.0.1-SNAPSHOT` y tener acceso a la versión instalada/publicada del artefacto. La librería no es una aplicación ejecutable independiente.
