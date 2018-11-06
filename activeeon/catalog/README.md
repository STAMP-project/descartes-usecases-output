## Catalog project URL: https://github.com/ow2-proactive/catalog

### Issue with integration test suite

In the catalog only the unit test will appear. Due to their design catalog integration test suite will not work with mutation testing.
"[...]if these tests are hitting a real db then they are not suitable for mutation testing as they unlikely to be repeatable." Henry Coles, https://github.com/hcoles/pitest/issues/170