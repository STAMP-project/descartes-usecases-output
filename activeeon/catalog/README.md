
# Catalog's Descartes Use Case

The report successfully found several issues from mutation test, the results are qualified as pseudo-tested and partially-tested. Partially-tested in this concept mean some coverage issues were found. Meanwhile pseudo-tested means some mutations survived. The former leaves room for improvement. We list all pseudo-tested code and the fix we found hereafter:


## Bugs found

* `createGenericInfoBucketData(org.ow2.proactive.catalog.repository.entity.BucketEntity) pseudo-tested`


This is actually a bug detected by the mutation. To solve this issue we check whenever the initialization was succesful. 

```diff
diff --git a/src/main/java/org/ow2/proactive/catalog/service/CatalogObjectService.java b/src/main/java/org/ow2/proactive/catalog/service/CatalogObjectService.java
index ed798a9..d3dad88 100644
--- a/src/main/java/org/ow2/proactive/catalog/service/CatalogObjectService.java
+++ b/src/main/java/org/ow2/proactive/catalog/service/CatalogObjectService.java
@@ -249,11 +249,20 @@ public class CatalogObjectService {

         List<KeyValueLabelMetadataEntity> keyValueMetadataEntities = KeyValueLabelMetadataHelper.convertToEntity(metadataList);

+        if(keyValueMetadataEntities == null){
+            throw new NullPointerException("Cannot build catalog object!");
+        }
+
         List<KeyValueLabelMetadataEntity> keyValues = CollectionUtils.isEmpty(metadataList) ? keyValueLabelMetadataHelper.extractKeyValuesFromRaw(catalogObjectEntity.getKind(),
                                                                                                                                                   rawObject)
                                                                                             : keyValueMetadataEntities;

         GenericInfoBucketData genericInfoBucketData = createGenericInfoBucketData(catalogObjectEntity.getBucket());
+
+        if(genericInfoBucketData == null){
+            throw new NullPointerException("Cannot build catalog object!");
+        }
+
         List<KeyValueLabelMetadataEntity> genericInformationWithBucketDataList = keyValueLabelMetadataHelper.replaceMetadataRelatedGenericInfoAndKeepOthers(keyValues,
                                                                                                                                                             genericInfoBucketData);
         byte[] workflowWithReplacedGenericInfo = genericInformationAdder.addGenericInformationToRawObjectIfWorkflow(rawObjecta,

```

## False positive (java native classes' methods)

Issue report entries that look like below:


```
createGenericInfoBucketData(org.ow2.proactive.catalog.repository.entity.BucketEntity) pseudo-testeorg.ow2.proactive.catalog.graphql.bean.argument.CatalogObjectNameWhereArgs$CatalogObjectNameWhereArgsBuilder (Issues: 7)

org.ow2.proactive.catalog.graphql.bean.argument.CatalogObjectBucketNameWhereArgs$CatalogObjectBucketNameWhereArgsBuilder (Issues: 7)

    notLike(java.lang.String) pseudo-tested
    like(java.lang.String) pseudo-tested
    gte(java.lang.String) pseudo-tested
    lt(java.lang.String) pseudo-tested
    lte(java.lang.String) pseudo-tested
    gt(java.lang.String) pseudo-tested
    ne(java.lang.String) pseudo-tested
```

These issues reported above claim that java.lang.String methods notLike, like, and so on are pseudo tested. In a deep analysis these methdos are used in a Generic way using java 8 specification. In the extract below we see that these methods are really the java native one and should not be mutated. We hence are safe to ignore methods that are native.


```java
public class CatalogObjectNameWhereArgs extends StringWhereArgs {

...

public class StringWhereArgs extends WhereArgs<String> {

...

public class WhereArgs<T> {
    public WhereArgs(T eq, T ne, T gt, T gte, T lt, T lte) {
        this.eq = eq;
        this.ne = ne;
        this.gt = gt;
        this.gte = gte;
        this.lt = lt;
        this.lte = lte;
    }

```


## Limitations

* `org.ow2.proactive.catalog.repository.specification.catalogobject.OrSpecification$OrSpecificationBuilder (Issues: 5)`
  * `operations(org.ow2.proactive.catalog.graphql.bean.common.Operations) pseudo-tested`

* `org.ow2.proactive.catalog.graphql.bean.argument.CatalogObjectWhereArgs$CatalogObjectWhereArgsBuilder (Issues: 2)`
  *  `metadataArg(org.ow2.proactive.catalog.graphql.bean.argument.CatalogObjectMetadataArgs) pseudo-tested`
  * `kindArg(org.ow2.proactive.catalog.graphql.bean.argument.CatalogObjectKindWhereArgs) pseudo-tested`

* `org.ow2.proactive.catalog.repository.specification.catalogobject.AndSpecification$AndSpecificationBuilder (Issues: 5)`
  *  `operations(org.ow2.proactive.catalog.graphql.bean.common.Operations) pseudo-tested`
  
The tests above are ok but at some point descartes seems to have troubles with spring. It mutates the constructor however
the class is created with a builder so this does not fail in practice. An extract of the test below shows that it
indeed tests the Operation class is not null and the correct type. It also test the whereArgs is not null.

```java
//...
        whereArgs = CatalogObjectWhereArgs.builder().orArg(ImmutableList.of(andwhere1, andwhere2)).build();
//...
 @Test
    public void testHandleMethod() throws Exception {
        Optional<Specification<CatalogObjectRevisionEntity>> specification = andFilterHandler.handle(whereArgs);
        assertThat(specification).isNotNull();
        assertThat(specification.get() instanceof OrSpecification).isTrue();
        OrSpecification orSpecification = (OrSpecification) specification.get();

```

## Issue with integration test suite

In the catalog only the unit test will appear. Due to the integration test suite design, it cannot work with mutation testing.
"[...]if these tests are hitting a real db then they are not suitable for mutation testing as they unlikely to be repeatable." Henry Coles, https://github.com/hcoles/pitest/issues/170

## Conclusion

Descartes makes it easy to find several cases for issues that are related to forgotten tests in the code. However it still need to support better inversion of control with the Spring framework. It is a good point that we could find a bug using descartes when hope in the future to see its effect in yet more complex internal repositories.

