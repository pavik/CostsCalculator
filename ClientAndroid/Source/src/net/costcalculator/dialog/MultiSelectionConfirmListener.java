
package net.costcalculator.dialog;

import java.util.ArrayList;

public interface MultiSelectionConfirmListener
{
    public void onMultiSelectionConfirmed(int dialogid,
            ArrayList<Integer> selectedItems, int param);
}
