# JSON - XML converter

A simple XML/JSON converter.

Conversion of XML to JSON:

Extract an element between tags and enclose it within double quotes.
Extract element content and enclose it within double quotes. If the element is empty, assign null (JSON value).

Conversion of JSON to XML:

Extract key between double quotes (XML element).
Extract element content from JSON value.
If value is not null, enclose JSON with <></>, else enclose within < />.

## Update

To deal with XML attributes, say that an attribute in JSON has `"@attribute_name": "attribute_value"` form.

See the examples below:

```html
<element attribute1 = "attribute1_value" … attributeN= "attributeN_value">content</element>
```

This XML objects would be mapped to this JSON object:

```json
{
    "element_name" :
    {
        "@attribute1" : "attribute1_value",

        "@attributeN" : "attributeN_value",
        "#element" : "content"
    }
}
```

