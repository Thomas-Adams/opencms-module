package org.adams.opencms.beans

enum ExplorerPermissions {

    READ("+r"), WRITE("+w"), VIEW("+v"), CONTROL("+c")

    private permission

    public ExplorerPermissions(String permission) {
        this.permission = permission
    }

    public String getText() {
        return this.permission;
    }
}