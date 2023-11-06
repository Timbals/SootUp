package sootup.java.bytecode.frontend;
/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997-2020 Raja Vallée-Rai, Andreas Dann, Markus Schmidt and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.jimple.visitor.ReplaceUseStmtVisitor;

/**
 * Frame of stack for an instruction. (see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-2.html#jvms-2.6">...</a> )
 *
 * @author Aaloan Miftah
 */
final class StackFrame {
  @Nonnull private final AbstractInsnNode insn;
  /**
   * TODO explain
   *
   * <p>Interestingly, none of the operations that need to have their inputs/outputs tracked produce
   * more than a single output, so this doesn't need to be a list.
   */
  @Nullable private Operand out;

  @Nonnull final ArrayList<Operand[]> in = new ArrayList<>(1);
  @Nonnull private final AsmMethodSource src;

  /**
   * Constructs a new stack frame.
   *
   * @param src source the frame belongs to.
   */
  StackFrame(@Nonnull AbstractInsnNode insn, @Nonnull AsmMethodSource src) {
    this.insn = insn;
    this.src = src;
  }

  void mergeOutput(@Nonnull Operand outputOperand) {
    if (out == null) {
      out = outputOperand;
    } else {
      if (out.stackLocal != null) {
        assert outputOperand.stackLocal == null;
        outputOperand.changeStackLocal(out.stackLocal);
      }
    }
  }

  /**
   * Merges the specified operands with the operands that were previously used with this
   * instruction.
   *
   * <p>To convert from the stack-based instructions to register-based instructions, all possible
   * combinations of branches need to be walked, because the contents of the operand stack might be
   * different when coming from different branches.
   *
   * <p>Take the following code as an example:
   *
   * <pre>System.out.println(n == 1 ? a : "two");</pre>
   *
   * If the first branch is taken `a` will be on the stack when the `println` gets invoked, if the
   * second branch is taken `"two"` will be on the stack when the `println` gets invoked. This
   * method will merge the two (or more) diverging operands by creating a local variable that the
   * value of both operands will be assigned to in their respective branches. That local will be
   * used when invoking the `println` method.
   *
   * <p>TODO somewhat easy to forget calling this after `pop`ing all required operands, maybe
   * automate this in some way?
   *
   * @param oprs the new operands.
   * @throws IllegalArgumentException if the number of new operands is not equal to the number of
   *     old operands.
   */
  void mergeInputs(@Nonnull Operand... oprs) {
    if (in.isEmpty()) {
      in.add(oprs);
      // There are no other operands to merge with
      return;
    }

    if (in.get(0).length != oprs.length) {
      throw new IllegalArgumentException("Invalid in operands length!");
    }

    if (oprs.length == 0) {
      // No operands to merge
      return;
    }

    for (int i = 0; i < oprs.length; i++) {
      Operand newOp = oprs[i];
      Local stack = null;

      // Search for a stack local that was already allocated for an operand in a different branch
      for (int j = 0; j != in.size(); j++) {
        stack = in.get(j)[i].stackLocal;
        if (stack != null) {
          break;
        }
      }

      // The incoming operand may already have a stack local allocated that can be re-used
      if (stack == null && newOp.stackLocal != null) {
        stack = newOp.stackLocal;
      }

      if (stack == null && in.get(0)[i].value.equivTo(newOp.value)) {
        // all branches have the same value,
        // and no stack local was allocated yet,
        // so no stack local is needed to converge the values
        continue;
      }

      // Didn't find any pre-allocated stack local from any operand.
      // So create a new stack local.
      // TODO use a special case when the statement is an assignment to a local since in that case
      //  we can use the local directly instead of creating a new stack local
      if (stack == null) {
        stack = src.newStackLocal();
      }

      /* add assign statement for prevOp */
      for (int j = 0; j != in.size(); j++) {
        Operand prevOp = in.get(j)[i];
        prevOp.changeStackLocal(stack);
      }
      newOp.changeStackLocal(stack);

      // TODO `in.get(0)` is weird because of the index?
      // TODO make it more obvious that this is only run the first time
      // replace the operand in the statement that *started* the merge
      ReplaceUseStmtVisitor replaceUseStmtVisitor =
          new ReplaceUseStmtVisitor(in.get(0)[i].value, stack);
      // TODO how to handle the same value being in the the statement multiple times but only one
      //  time because of the operand? (Something like `System.out.println(operand, "hello")` with
      //  the operand also having the value "two")
      //  this might require a callback(?) to change the statement; alternative we could do the
      //  merging *before* constructing the statement and then replace the statement if it differs
      //  from an already existing one
      //  This actually works right now because the `ReplaceUseExprVisitor` only checks object
      //  equality meaning the
      //  two instances of the constant are different and only the correct instance is replaced
      Stmt oldStatement = this.src.getStmt(this.insn);
      // TODO `oldStatement` might not exist when a STORE instruction was used to set the
      //  stackLocal
      if (oldStatement != null) {
        oldStatement.accept(replaceUseStmtVisitor);
        this.src.replaceStmt(oldStatement, replaceUseStmtVisitor.getResult());
      }
    }

    in.add(oprs);
  }
}
