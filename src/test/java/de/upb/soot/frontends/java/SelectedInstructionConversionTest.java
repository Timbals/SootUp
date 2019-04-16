package de.upb.soot.frontends.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import categories.Java8Test;
import de.upb.soot.core.Body;
import de.upb.soot.core.SootClass;
import de.upb.soot.core.SootMethod;
import de.upb.soot.jimple.common.stmt.IStmt;
import de.upb.soot.signatures.DefaultSignatureFactory;
import de.upb.soot.signatures.JavaClassSignature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/** @author Linghui Luo */
@Category(Java8Test.class)
public class SelectedInstructionConversionTest {

  private WalaClassLoader loader;
  private DefaultSignatureFactory sigFactory;
  private JavaClassSignature declareClassSig;

  @Before
  public void loadClassesWithWala() {
    String srcDir = "src/test/resources/wala-tests/";
    loader = new WalaClassLoader(srcDir, null);
    sigFactory = new DefaultSignatureFactory();
  }

  @Ignore
  public void test1() {
    // TODO FIX IT
    declareClassSig = sigFactory.getClassSignature("alreadywalaunittests.InnerClassAA.AA");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "makeAB",
                declareClassSig,
                "alreadywalaunittests.InnerClassAA.AB",
                Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void test2() {
    declareClassSig = sigFactory.getClassSignature("AnonymousClass");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "method", declareClassSig, "void", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();

    Body body = method.getActiveBody();
    assertNotNull(body);

    List<String> actualStmts =
        body.getStmts().stream()
            .map(IStmt::toString)
            .collect(Collectors.toCollection(ArrayList::new));

    List<String> expectedStmts =
        Stream.of(
                "r0 := @this: AnonymousClass",
                "$r1 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(7)",
                "$r2 = new AnonymousClass$2",
                "$i0 = 0 - 4",
                "specialinvoke $r2.<AnonymousClass$2: void <init>(int)>($i0)",
                "$r3 = <java.lang.System: java.io.PrintStream out>",
                "$i1 = interfaceinvoke $r2.<AnonymousClass$Foo: int getValue()>()",
                "virtualinvoke $r3.<java.io.PrintStream: void println(int)>($i1)",
                "$r4 = <java.lang.System: java.io.PrintStream out>",
                "$i2 = interfaceinvoke $r2.<AnonymousClass$Foo: int getValueBase()>()",
                "virtualinvoke $r4.<java.io.PrintStream: void println(int)>($i2)",
                "return")
            .collect(Collectors.toCollection(ArrayList::new));

    assertEquals(expectedStmts, actualStmts);
  }

  @Test
  public void test3() {
    declareClassSig = sigFactory.getClassSignature("alreadywalaunittests.InnerClassAA");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "doAllThis", declareClassSig, "void", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();

    Body body = method.getActiveBody();
    assertNotNull(body);

    List<String> actualStmts =
        body.getStmts().stream()
            .map(IStmt::toString)
            .collect(Collectors.toCollection(ArrayList::new));

    List<String> expectedStmts =
        Stream.of(
                "r0 := @this: alreadywalaunittests.InnerClassAA",
                "$r1 = r0",
                "$r2 = new alreadywalaunittests.InnerClassAA$AA",
                "specialinvoke $r2.<alreadywalaunittests.InnerClassAA$AA: void <init>(alreadywalaunittests.InnerClassAA)>($r0)",
                "$r3 = new alreadywalaunittests.InnerClassAA$AA",
                "specialinvoke $r3.<alreadywalaunittests.InnerClassAA$AA: void <init>(alreadywalaunittests.InnerClassAA)>($r1)",
                "$r2 = $r3",
                "$r4 = virtualinvoke $r2.<alreadywalaunittests.InnerClassAA$AA: alreadywalaunittests.InnerClassAA$AB makeAB()>()",
                "$r1.<alreadywalaunittests.InnerClassAA: int a_x> = 5",
                "$i0 = virtualinvoke $r4.<alreadywalaunittests.InnerClassAA$AB: int getA_X_from_AB()>()",
                "$r5 = <java.lang.System: java.io.PrintStream out>",
                "virtualinvoke $r5.<java.io.PrintStream: void println(int)>($i0)",
                "$i1 = virtualinvoke $r4.<alreadywalaunittests.InnerClassAA$AB: int getA_X_thru_AB()>()",
                "$r6 = <java.lang.System: java.io.PrintStream out>",
                "virtualinvoke $r6.<java.io.PrintStream: void println(int)>($i1)",
                "virtualinvoke $r2.<alreadywalaunittests.InnerClassAA$AA: void doSomeCrazyStuff()>()",
                "return")
            .collect(Collectors.toCollection(ArrayList::new));

    assertEquals(expectedStmts, actualStmts);
  }

  @Test
  public void test4() {
    declareClassSig = sigFactory.getClassSignature("alreadywalaunittests.InnerClassAA");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "main", declareClassSig, "void", Collections.singletonList("java.lang.String[]")));
    assertTrue(m.isPresent());
    SootMethod method = m.get();

    Body body = method.getActiveBody();
    assertNotNull(body);

    List<String> actualStmts =
        body.getStmts().stream()
            .map(IStmt::toString)
            .collect(Collectors.toCollection(ArrayList::new));

    List<String> expectedStmts =
        Stream.of(
            "$r0 := @parameter0: java.lang.String[]",
            "$r1 = new alreadywalaunittests.InnerClassAA",
            "specialinvoke $r1.<alreadywalaunittests.InnerClassAA: void <init>()>()",
            "virtualinvoke $r1.<alreadywalaunittests.InnerClassAA: void doAllThis()>()",
            "return")
            .collect(Collectors.toCollection(ArrayList::new));

    assertEquals(expectedStmts, actualStmts);
  }

  @Test
  public void test5() {
    declareClassSig = sigFactory.getClassSignature("alreadywalaunittests.InnerClassAA");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "<init>", declareClassSig, "void", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();

    Body body = method.getActiveBody();
    assertNotNull(body);

    List<String> actualStmts =
        body.getStmts().stream()
            .map(IStmt::toString)
            .collect(Collectors.toCollection(ArrayList::new));

    List<String> expectedStmts =
        Stream.of(
            "r0 := @this: alreadywalaunittests.InnerClassAA",
            "specialinvoke r0.<java.lang.Object: void <init>()>()",
            "return")
            .collect(Collectors.toCollection(ArrayList::new));

    assertEquals(expectedStmts, actualStmts);
  }

  @Test
  public void test6() {
    declareClassSig = sigFactory.getClassSignature("foo.bar.hello.world.ArraysAndSuch");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "main", declareClassSig, "void", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();

    Body body = method.getActiveBody();
    assertNotNull(body);

    List<String> actualStmts =
        body.getStmts().stream()
            .map(IStmt::toString)
            .collect(Collectors.toCollection(ArrayList::new));
  // TODO does invoking wait(long, int) require upcast? goto really with ?= branch?
    // TODO is $r21 = newarray (int[][][][])[2] correct? because we have n-dim array
    // TODO Other than that, seems fine
    List<String> expectedStmts =
        Stream.of(
            "r0 := @this: alreadywalaunittests.InnerClassAA",
            "specialinvoke r0.<java.lang.Object: void <init>()>()",
            "return")
            .collect(Collectors.toCollection(ArrayList::new));

    assertEquals(expectedStmts, actualStmts);
  }

  @Test
  public void testThrowInstruction() {
    declareClassSig = sigFactory.getClassSignature("FooEx1");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature("bar", declareClassSig, "void", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testSwitchInstruction() {
    declareClassSig = sigFactory.getClassSignature("bugfixes.DoWhileInCase");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "main", declareClassSig, "void", Collections.singletonList("java.lang.String[]")));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testLoadMetadataInstruction() {
    declareClassSig = sigFactory.getClassSignature("javaonepointfive.EnumSwitch$Palo");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "valueOf",
                declareClassSig,
                "javaonepointfive.EnumSwitch$Palo",
                Collections.singletonList("java.lang.String")));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testCheckCastInstruction() {
    declareClassSig = sigFactory.getClassSignature("javaonepointfive.EnumSwitch$Palo");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "valueOf",
                declareClassSig,
                "javaonepointfive.EnumSwitch$Palo",
                Collections.singletonList("java.lang.String")));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testEnclosingObjectReference() {
    declareClassSig = sigFactory.getClassSignature("alreadywalaunittests.InnerClassAA$AA");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "doSomeCrazyStuff", declareClassSig, "void", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testEnclosingObjectReferenceWithFieldCreation() {
    declareClassSig = sigFactory.getClassSignature("alreadywalaunittests.InnerClassAA$AA");
    Optional<SootClass> m = loader.getSootClass(declareClassSig);
    assertTrue(m.isPresent());
    // TODO. replace the next line with assertions.
    Utils.print(m.get(), false);
  }

  @Test
  public void testAstLexicalRead() {
    declareClassSig = sigFactory.getClassSignature("AnonymousClass$1");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "getValueBase", declareClassSig, "int", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testAstLexicalReadFromField() {
    declareClassSig = sigFactory.getClassSignature("Scoping2");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "main", declareClassSig, "void", Collections.singletonList("java.lang.String[]")));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testAstLexicalReadWithFieldCreation() {
    declareClassSig = sigFactory.getClassSignature("AnonymousClass$1");
    Optional<SootClass> m = loader.getSootClass(declareClassSig);
    assertTrue(m.isPresent());
    // TODO. replace the next line with assertions.
    Utils.print(m.get(), false);
  }

  @Test
  public void testAstLexicalWrite() {
    declareClassSig = sigFactory.getClassSignature("foo.bar.hello.world.InnerClasses");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "anonymousCoward", declareClassSig, "java.lang.Object", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testAstLexicalWriteToField() {
    declareClassSig = sigFactory.getClassSignature("foo.bar.hello.world.InnerClasses");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "supportLocalBusiness",
                declareClassSig,
                "java.lang.Object",
                Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testAstLexicalWriteWithFieldCreation() {
    declareClassSig = sigFactory.getClassSignature("foo.bar.hello.world.InnerClasses");
    Optional<SootClass> m = loader.getSootClass(declareClassSig);
    assertTrue(m.isPresent());
    // TODO. replace the next line with assertions.
    Utils.print(m.get(), false);
  }

  @Test
  public void testAstAssertInstruction() {
    declareClassSig = sigFactory.getClassSignature("MiniaturSliceBug");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "validNonDispatchedCall",
                declareClassSig,
                "void",
                Collections.singletonList("IntWrapper")));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testAstAssertInstructionWithFieldCreation() {
    declareClassSig = sigFactory.getClassSignature("MiniaturSliceBug");
    Optional<SootClass> m = loader.getSootClass(declareClassSig);
    assertTrue(m.isPresent());
    // TODO. replace the next line with assertions.
    Utils.print(m.get(), false);
  }

  @Test
  public void testMonitorInstruction() {
    declareClassSig = sigFactory.getClassSignature("Monitor");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "incr", declareClassSig, "void", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    Utils.print(method, false);
  }

  @Test
  public void testGetCaughtExceptionInstruction() {
    declareClassSig = sigFactory.getClassSignature("Exception1");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature(
                "main", declareClassSig, "void", Collections.singletonList("java.lang.String[]")));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  @Test
  public void testArrayInstructions() {
    declareClassSig = sigFactory.getClassSignature("Array1");
    Optional<SootMethod> m =
        loader.getSootMethod(
            sigFactory.getMethodSignature("foo", declareClassSig, "void", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();
    // TODO. replace the next line with assertions.
    Utils.print(method, false);
  }

  public void testAstLexialReadWithMultipleAccesses() {
    // TODO
  }

  public void testAstLexialWriteWithMultipleAccesses() {
    // TODO
  }
}
