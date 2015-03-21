var c = cljs.core;

describe("Testing ISeqable - seq", function() {
  it("seq should create a lazyseq from arrays and objects", function() {
    expect(cljs.core._EQ_(
             c.seq(["a", "b"]),
             c.list("a", "b"))).toBe(true);
    expect(cljs.core._EQ_(
            c.seq({a:1}),
            c.list(c.vector("a",1)))).toBe(true);
  });
});

describe("Testing ICounted - count", function() {
  it("can be used with `count`", function() {
    expect(c.count([1,2,3])).toBe(3);
    expect(c.count({a:1,b:2,c:3})).toBe(3);
  });
});

describe("Testing ILookup - lookup", function() {
  it("can be used with `get`, `get-in` and `contains`", function() {
    expect(c.get([1,2,3], 0)).toBe(1);
    expect(c.get({a:1,b:2,c:3}, "b")).toBe(2);
    expect(c.get_in({a:1}, ["a"])).toBe(1);
    expect(c.contains_QMARK_({a:1}, "a")).toBe(true);
  });
});

describe("Testing ITransientAssociative - assoc! and dissoc!", function() {
  it("can be used with `get`, `get-in` and `contains`", function() {
    expect(c.assoc_BANG_([1,2,3], 0, 2)).toEqual([2,2,3]);
    expect(c.assoc_BANG_({a:1,b:2,c:3}, "a", 2)).toEqual({a:2,b:2,c:3});
  });
});


describe("Testing ITransientMap - dissoc!", function() {
  it("can be used with `dissoc!`", function() {
    //expect(c.dissoc_BANG_([1,1,1], 0)).toEqual([undefined,1,1]);
    expect(c.dissoc_BANG_({a:1,b:2,c:3}, "a")).toEqual({b:2,c:3});
  });
});

describe("Testing IEmptyableCollection - empty", function() {
  it("can be used with `empty`", function() {
    expect(c.empty([1,2,3])).toEqual([]);
    expect(c.empty({a:1,b:2,c:3})).toEqual({});
  });
});

describe("Testing IAssociative - assoc", function() {
  it("can be used with `assoc`", function() {
    expect([undefined, 1, 1]).toEqual([undefined, 1,1]);
    expect(c.assoc([1,2,3], 0, 2)).toEqual([2,2,3]);
    //expect(c.dissoc([1,2,3], 1)).toEqual([1,undefined,3]);
    expect(c.assoc({a:1,b:2,c:3}, "a", 2)).toEqual({a:2,b:2,c:3});
    expect(c.dissoc({a:1,b:2,c:3}, "a")).toEqual({b:2,c:3});
  });
});

describe("Testing IMap - dissoc", function() {
  it("can be used with `dissoc`", function() {
    //expect(c.dissoc([1,2,3], 0)).toSatisfy([undefined,2,3]);
    expect(c.dissoc({a:1,b:2,c:3}, "a")).toEqual({b:2,c:3});
  });
});

describe("Testing ICollection - conj", function() {
  it("can be used with `dissoc`", function() {
    expect(c.conj([1,2,3], 4)).toEqual([1,2,3,4]);
    expect(c.conj({a:1,b:2,c:3}, ["d",4])).toEqual({a:1,b:2,c:3,d:4});
  });
});

