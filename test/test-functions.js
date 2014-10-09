
var p = purnam["native$"].functions;
var c = cljs.core;

describe("Testing js-delete", function() {
  it("should create delete a key from the object", function() {
    var tmp = {a: 1, b: 2};
    c.js_delete(tmp, "a")
    expect(tmp).toEqual({b:2})
  });
});

describe("Testing nested-val", function() {
  it("should create a nested value out of an array an value", function() {
    expect(p.nested_val(["a", "b", "c"], 1))
            .toEqual({a: {b: {c:1}}});
  });
});

describe("Testing nested-delete", function() {
  it("should create a delete a key from a value", function() {    
    var tmp = {a: {b:1, c:2}};
    p.nested_delete(["a", "b"], tmp)  
    expect(tmp).toEqual({a: {c: 2}});
    
  });
});

describe("Testing aget-in", function() {
  it("should get a value", function() {    
    var tmp = {a: {b:{c:2}}};
    expect(p.aget_in(tmp, ["a", "b"])).toEqual({c: 2});
    expect(p.aget_in(tmp, ["a", "b", "c"])).toEqual(2); 
  });
});


describe("Testing aset-in", function() {
  it("should set a nested value", function() {    
    var tmp = {a: {b:{c:1}}};
    p.aset_in(tmp, ["a", "b", "c"], 2);
    expect(tmp).toEqual({a: {b:{c:2}}}); 
  });
});

describe("Testing js-str-key", function() {
  it("should return the string value for an input", function() {    
    expect(p.js_strkey("hello")).toEqual("hello");
    expect(p.js_strkey(c.keyword("hello"))).toEqual("hello"); 
  });
});

describe("Testing js-type", function() {
  it("should return the value type", function() {    
    expect(p.js_type(1)).toEqual("number"); 
    expect(p.js_type("")).toEqual("string"); 
    expect(p.js_type({})).toEqual("object"); 
    expect(p.js_type([])).toEqual("array"); 
    expect(p.js_type(function(){})).toEqual("function"); 
    expect(p.js_type(c.keyword("a"))).toEqual("cljs.core/Keyword"); 
  });
});

describe("Testing js-lookup", function() {
  it("should lookup the value of an object from either a string or keyword", function() {    
    expect(p.js_lookup({'a':1}, 'a')).toEqual(1);
    expect(p.js_lookup({'a':1}, c.keyword('a'))).toEqual(1); 
  });
});

describe("Testing js-range", function() {
  it("should give the range of values from start to end", function() {    
    expect(p.js_range(10)).toEqual([0,1,2,3,4,5,6,7,8,9]);
    expect(p.js_range(0, 10, 3)).toEqual([0,3,6,9]); 
  });
});

describe("Testing js-assoc", function() {
  it("should associate key and value to an object", function() {    
    var tmp = {a:1};
    expect(p.js_assoc(tmp, "a", 2, "b", 3)).toEqual({a: 2, b: 3}); 
    expect(tmp).toEqual({a: 2, b: 3}); 
  });
});

describe("Testing js-dissoc", function() {
  it("should dissociate key from an object", function() {    
    var tmp = {a:1, b:2};
    expect(p.js_dissoc(tmp, "a", "b")).toEqual({}); 
    expect(tmp).toEqual({}); 
  });
});

describe("Testing js-empty", function() {
  it("removes all items from an object or array", function() {    
    var tmp = {a:1, b:2};
    expect(p.js_empty(tmp)).toEqual({}); 
    expect(tmp).toEqual({}); 
    
    var tmp = ['a', 1, 'b', 2];
    expect(p.js_empty(tmp)).toEqual([]); 
    expect(tmp).toEqual([]); 
    
  });
});

describe("Testing js-merge", function() {
  it("merges two objects together", function() {    
    var tmp = {a:1, b:2};
    expect(p.js_merge(tmp, {b:3, c:4})).toEqual({a:1,b:3, c:4}); 
    expect(tmp).toEqual({a:1,b:3, c:4});
  });
});

describe("Testing js-merge-nil", function() {
  it("merges two objects together. keys existing in the first map are not replaced", function() {    
    var tmp = {a:1,b:2};
    expect(p.js_merge_nil(tmp, {b:3,c:4})).toEqual({a:1,b:2,c:4}); 
    expect(tmp).toEqual({a:1,b:2,c:4});
  });
});

describe("Testing js-replace", function() {
  it("replaces the contents of a object with another", function() {    
    var tmp = {a:1,b:2};
    expect(p.js_replace(tmp, {b:3,c:4})).toEqual({b:3,c:4}); 
    expect(tmp).toEqual({b:3,c:4});
  });
});

describe("Testing js-equals", function() {
  it("checks for equality in object or array", function() {    
    var tmp = {a:1,b:[1,2,3]};
    expect(p.js_equals(tmp, {a:1,b:[1,2,3]})).toEqual(true);
    expect(p.js_equals(tmp, {a:1,b:[1,2,4]})).toEqual(false);
  });
});

describe("Testing js-copy", function() {
  it("makes a shallow clone of the object", function() {    
    var tmp = {a:1,b:[1,2,3]};
    var tmp2 = p.js_copy(tmp);
    expect(tmp).toEqual(tmp2);
  });
});

describe("Testing js-deep-copy", function() {
  it("makes a deep clone of the object, preserving cyclic structure", function() {    
    var tmp = {a:1,b:[1,2,3]};
    var tmp2 = p.js_deep_copy(tmp);
    expect(tmp).toEqual(tmp2);
  });
});

