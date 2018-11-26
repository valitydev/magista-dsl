# magista-dsl

Для формирования запросов к данным библиотека предоставляет DSL в JSON формате, который основан на Elasticsearch [Query DSL](https://www.elastic.co/guide/en/elasticsearch/reference/current/_introducing_the_query_language.html). 

Общий формат запроса выглядит следующим образом:

```json
{
  "query": {
    "<query_type>": {
      "<param>": "<val>"
    },
    "<query_param>": "<val>"
  }
}
```

`<query_type>` - тип запроса, который требуется выполнить. `<query_param>` зависит от типа запроса.

`<query_param>` - параметр запроса, может включать:

1. Для запросов на выборку по моделям:

    1. `from` (__deprecated__) - (0-based) определяет, с какой записи результирующей выборки следует начать. Вместо этого поля необходимо использовать continuationToken, на основании которого оно будет вычисляться. 
    2. `size` - определяет, сколько максимум записей следует вернуть, начиная с `from`.
