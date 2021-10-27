# POD-TPE2
Trabajo práctico número 2 de Programación de Objetos Distribuidos: Árboles

## Integrantes

- [Brandy Tobias](https://github.com/tobiasbrandy)
- [Pannunzio Faustino](https://github.com/Fpannunzio)
- [Sagues Ignacio](https://github.com/isagues)
- [Vazquez Ignacio](https://github.com/igvazquez)

## Compilación

Para la compilación del proyecto es necesario tener Java 11 o superior y basta con correr `mvn clean install` en la raíz del proyecto. Esto genera los 2 archivos necesarios para la ejecución. Esto son 2 archivos `.tar.gz` ubicados en `server/target` y `client/target`.

## Ejecución

Habiendo descomprimido los archivos generados en una carpeta a elección, se puede proceder a la ejecución.

Dentro de cada una de las carpetas descomprimidas están los archivos `.sh` necesarios para la ejecución.

### Ejecución Servidores Hazelcast

El primer paso es correr los servidores Hazelcast que serán los encargados de resolver las queries Map-Reduce. Para esto es necesario correr `run-server.sh`, que se encuentra en la carpeta del servidor.

Los servidores están configurados para que el descubrimiento se realice mediante TCP/IP, por lo que se debera proveer mediante parámetro el address de al menos uno de los servidores ya establecidos.

Todos los parámetros son opcionales y deberán ser provistos mediante propiedades del sistema.

#### Parámetros
- `name`: El group name del cluster. Por default, `g16`.
- `pass`: El group pass del cluster. Por default, `g16-pass`.
- `interfaces`: Lista de interfaces por las cuales escuchar, utilizando como separador el caracter `;`. Por default, `127.0.0.1`.
- `members`: Lista de pares dirección_IP y puerto de nodos ya existentes del cluster que se utilizaran en el proceso de descubrimiento del mismo mediante el mecanismo TCP/IP. El formato es `xx.xx.xx.xx:XXXX` utilizando como separador el caracter `,`. Por default no se consideran miembros (primer nodo del cluster).
- `port`: Puerto por el cual escuchar. Por default tomamos el estándar de Hazelcast, es decir, `5701`. La funcionalidad de auto-incremento de puerto está intencionalmente apagada, por lo que de no poder conectarse en este puerto, el servidor no será levantado.
- `mgmtCenterUrl`: Url de un Management Center al que se quiera conectar. Por default, `http://localhost:32768/mancenter/`. Si bien se incluye la opción, la misma no fue utilizada, por lo que no se garantiza su funcionamiento.
- `mgmtCenterEnable`: Booleano que indica si se quiere que el nodo se conecte a un Management Center. Por default, `false`. Si bien se incluye la opción, la misma no fue utilizada, por lo que no se garantiza su funcionamiento.

##### Ejemplo de Uso
```shell
./run-server.sh -Dname=g16 -Dpass=g16-pass -Dinterfaces=127.0.0.1 -Dmembers=127.0.0.1:5701,127.0.0.1:5702 -Dport=5703
```

### Ejecución Queries

Una vez levantado el cluster por completo, ya podemos ejecutar las queries.

Existen 5 clientes, uno por cada query, con el nombre `query#`, donde # es el número de query. Todos los clientes comparten ciertos parámetros generales, y luego cada query puede definir parámetros adicionales.

#### Parámetros Generales

- `city`: El nombre de la ciudad del dataset (CSV) del cual consumir los datos, el cual indica la estructura que tendrán los mismos. Los valores soportados actualmente son `VAN` (Vancouver) y `BUE` (Buenos Aires). Este parámetro es el único requerido.
- `inPath`: Path relativo o absoluto al directorio donde se encuentran los dataset (CSV). Los mismos deberán ser nombrados como `arboles{city}.csv` y `barrios{city}.csv`, donde {city} es el nombre de la ciudad seleccionada. Por default se utiliza el directorio actual.
- `outPath`: Path relativo o absoluto al directorio donde se crearán los archivos con los resultados de la query. Los mismos serán nombrados como `query#.csv` y `time#.txt`, donde # es el número de query. Si bien los archivos serán creados de no existir (o truncados de existir), es necesario que el directorio seleccionado sí exista. Por default se utiliza el directorio actual.
- `name`: El group name del cluster Hazelcast al que se quiere conectar. Por default, `g16`.
- `pass`: El group pass del cluster Hazelcast al que se quiere conectar. Por default, `g16-pass`.
- `addresses`: Lista de direcciones (IP y puerto) candidatas que el cliente usara para establecer la primera conexión con el cluster Hazelcast. El formato es `xx.xx.xx.xx:XXXX` utilizando el caracter `;` como separador. Por default, `127.0.0.1:5701`.
- `charset`: El nombre del charset que poseen los dataset (CSV) de entrada, o alguno compatible. De no serlo, la lectura de los mismos fallará. Por default, `ISO-8859-1`.

##### Ejemplo de Uso

```shell
./query# -Dcity=BUE -DinPath=/home/in -DoutPath=/home/out -Dname=g16 -Dpass=g16-pass -Daddresses='127.0.0.1:5701;127.0.0.1:5702' [...parametros_particulares]
```

#### Parámetros Particulares de Cada Query

##### Query 3

- `n`: Cantidad de barrios a listar en la respuesta. Por default, se listan la cantidad maxima que soporta un int, es decir, `2^31 - 1`

##### Query 5

- `neighbourhood`: El nombre del barrio a considerar durante la query. Requerido.
- `commonName`: El nombre de la especie de arbol a considerar durante la query. Requerido.

### Ejemplos Dataset de Entrada (Buenos Aires)

#### arbolesBUE.csv
```csv
nro_registro;tipo_activ;comuna;manzana;calle_nombre;calle_altura;direccion_normalizada;nombre_cientifico;estado_plantera;ubicacion_plantera;nivel_plantera;diametro_altura_pecho;altura_arbol
26528;Lineal;3; ;Riobamba;0;RIOBAMBA 74;No identificado;Ocupada;Regular;A nivel;142;
314678;Lineal;7;161;La Portena;0;LA PORTEnA 90;Fraxinus pennsylvanica;Ocupada;Regular;A nivel;142;10
341086;Lineal;10;341;Pergamino;0;PERGAMINO 21;Fraxinus excelsior;Ocupada;Regular;A nivel;143;
218919;Lineal;15;599;Viale Luis;0;VIALE, LUIS 74;Fraxinus pennsylvanica;Ocupada;Regular;A nivel;143;
228534;Lineal;15;883;Fraga;0;FRAGA 128;No identificado;Ocupada;Regular;A nivel;143;3
230027;Lineal;15;883;Guevara;0;GUEVARA 145;Melia azedarach;Ocupada;Regular;A nivel;143;14
228649;Lineal;15;883;Fraga;0;FRAGA 154;No identificado;Ocupada;Regular;A nivel;143;2
261678;Lineal;15;637;Arganaraz;0; ;Fraxinus pennsylvanica;Ocupada;Regular;A nivel;143;2
344063;Lineal;10;341;Azul;0;AZUL 72;Fraxinus excelsior;Ocupada;Regular;Bajo nivel;143;9
211624;Lineal;15;637;Arganaraz;0; ;No identificado;Ocupada;Regular;Bajo nivel;143;3
34318;Lineal;3; ;Azcuenaga;0;AZCUENAGA 61;No identificado;Ocupada;Regular;Elevada;143;
```

#### barriosBUE.csv
```csv
nombre;habitantes
2;149607
14;227003
15;182427
6;185067
7;241065
13;236107
8;227495
```

### Ejemplos Salida

#### query1.csv
```csv
NEIGHBOURHOOD;TREES
12;38818
11;37340
9;36405
10;33421
4;32327
15;29587
13;28287
7;25608
14;22417
8;21905
6;15591
5;15224
3;13320
1;12723
2;7115
```

#### time1.txt
```txt
18/10/2021 01:28:37:8524 - Inicio de la lectura del archivo
18/10/2021 01:29:03:6710 - fin de lectura del archivo
18/10/2021 01:29:03:6811 - Inicio del trabajo map/reduce
18/10/2021 01:29:09:0387 - Fin del trabajo map/reduce
```
