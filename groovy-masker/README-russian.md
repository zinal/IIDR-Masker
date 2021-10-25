# IIDR-Masker (groovy-masker)
Выполнение трансформаций колонок в среде IBM Change Data Capture 
на языке программирования Groovy.

Скомпилированный код (готовый для установки) в виде файла `CdcGroovy.class` 
находится в каталоге `bin`.

Для установки поместите файл `CdcGroovy.class` в каталог `{cdc-install-dir}/lib`.
Для работы также потребуется библиотека реализации языка Groovy 
`groovy-3.0.9.jar`, которую можно скачать в составе 
[дистрибутива Groovy](https://groovy.jfrog.io/ui/native/dist-release-local/groovy-zips/apache-groovy-sdk-3.0.9.zip). 
Эту библиотеку необходимо поместить в каталог `{cdc-install-dir}/lib`, 
а затем зарегистрировать в файле `{cdc-install-dir}/instance/{instance-name}/conf/system.cp`
в виде значения `lib/groovy-3.0.9.jar`. 

Скомпилированный код устанавливается на агент-источник, если будут
использоваться порождённые колонки (derived columns), либо на агент-получатель,
если будут использоваться прямое сопоставление порождённых выражений 
(derived expressions) колонкам получателя.

Для сборки проекта с помощью Maven необходимо извлечь файл `lib/ts.jar`
из вашей инсталляции агента IBM CDC. Библиотеку `ts.jar` необходимо
поместить в локальный репозиторий Maven:

```bash
mvn install:install-file -Dfile=`pwd`/ts.jar \
  -DgroupId=com.ibm.iidr -DartifactId=ts -Dversion=11.4.0.4.5607 -Dpackaging=jar
```

После этого сборку и открытие проекта в различных Java IDE
можно осуществлять с помощью файла `pom.xml`.

Альтернативный метод сборки заключается в копировании файла
`CdcGroovy.java` в каталог `{cdc-install-dir}/lib` и последующей сборки
с помощью команды `javac`:

```bash
javac CdcGroovy.java -classpath ts.jar:groovy-3.0.9.jar
```

Скрипты на языке Groovy следует разместить в каталоге `{user.home}/cdcgroovy`
(подкаталоге `cdcgroovy` домашнего каталога пользователя).
Имена файлов должны соответствовать названиям скриптов: `{script-name}.groovy`.
Затем в настройках подписки IBM CDC вы можете обращаться к скриптам
в порождённых выражениях (derived expressions) с использованием
следующего синтаксиса:
```
%USERFUNC("JAVA","CdcGroovy","script-name", COLUMN1, COLUMN2, ...)
```

Каждый скрипт Groovy должен реализовать функцию `invoke()` с как
минимум одним аргументом (туда передаётся имя скрипта).
Дополнительные аргументы по количеству и типам данных должны
соответствовать составу передаваемых параметров вызова.
Можно реализовать сразу несколько функций `invoke()`
в одном скрипте Groovy с разным количеством и типами данных аргументов.
Также поддерживаются варианты функции `invoke()`
с переменным количеством аргументов.

Пример простого скрипта Groovy для расчёта хеша SHA-1:

```Groovy
def invoke(String scriptName, Object value) {
   if (value == null)
     return null;
   return "sha1:" + value.toString().digest("SHA-1");
}
```
