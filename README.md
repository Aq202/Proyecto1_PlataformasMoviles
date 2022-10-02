# Aplicación de Finanzas Personales

### Descripción del proyecto
Uno de los aspectos más importantes en la vida de cada persona es la forma en la que administra sus finanzas personales; sin embargo, en la mayoría de los casos, esta tarea es relegada a un segundo plano, provocando un desorden en el manejo del dinero y limitando la adquisición de hábitos de ahorro y consumo responsable. Un mal manejo de los ingresos y gastos puede tener grandes repercusiones, tales como la aparición excesiva de gastos fantasmas, incurrencia en gastos desproporcionados e innecesarios, adquisición de deudas, e incluso el aumento en el estrés y la falta de tranquilidad de una persona. Una de las mayores limitantes al momento de ejecutar una planificación financiera adecuada y llevar un control exhaustivo de los ingresos y gastos, es lo tedioso que este proceso puede resultar y lo difícil que puede llegar a ser implementarlo dentro de la rutina diaria de cada persona. 

Es por ello que, a través de una aplicación móvil, buscamos ofrecer una alternativa que permita recopilar de una manera sencilla, accesible y ordenada toda la información financiera de una persona; y así, hacer de su conocimiento el estado real de su propio patrimonio. A través de dicha aplicación, será capaz de visualizar sus ingresos y gastos en periodos de tiempo determinados, efectuar un manejo de las operaciones en cada una de sus cuentas, organizar sus ingresos y gastos en categorias y demás funcionalidades que le permitan mantener un manejo óptimo y transparente de sus propios bienes. 

## Servicios

* **API propia:** se va a optar por la realización de una API propia, utilizando como tecnología backed *Node JS*. A través de este servicio vamos a poder manejar tareas como: la autenticación de usuarios, conexión y administración de bases de datos remotas, acceso a recursos multimedia compartidos, realizar operaciones entre diferentes usuarios, entre otros.
* **Base de datos remota:** se utilizara *MongoDB* para la creación y gestión de una base de datos no relacional, con el objetivo de almacenar los datos de los usuarios en un servidor en la nube.

## Librerias

* **Coil:** se va a emplear para la carga y despliegue de imagenes alojadas en el servidor remoto (principalmente las fotos de perfil de los diferentes usuarios).
* **Retrofit:** se va a utilizar para ejecutar peticiones http y así poder consumir cada uno de los servicios que va a ofrecer nuetra API.
*  **Room:** a través de esta libreria vamos a manejar el almacenamiento local por medio de SQLite. Esto con el objetivo de proporcionar un funcionamiento offline a la aplicación, guardando de manera local los datos poporcionados por la API con anterioridad.

## Funcionalidades

* Registro de usuarios.
* Autenticación y login de usuarios.
* Creación y administración de cuentas (efectivo, cuentas de banco, etc.).
* Registro de ingresos y gastos, agrupándolos por categorías.
* Visualizar operaciones por tipo de cuenta.
* Visualizar operaciones recientes.
* Realizar transferencia entre cuentas.

#### Funcionalidades extra
*El desarrollo e implementación de las funcionalidades extra se encuentran sujetas a la disponibilidad de tiempo para su realización.*

* Crear y programar pagos pendientes.
* Operaciones favoritas.
* Contactos y operaciones entre usuarios.

