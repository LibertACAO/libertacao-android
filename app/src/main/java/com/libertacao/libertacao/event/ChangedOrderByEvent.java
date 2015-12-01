package com.libertacao.libertacao.event;

public class ChangedOrderByEvent {
    public final int selectedOrderBy;

    public ChangedOrderByEvent(int selectedOrderBy) {
        this.selectedOrderBy = selectedOrderBy;
    }
}
