package com.example.shoppingassistant.model;

public class Data {

    private ItemType type;
    private String amount;
    private String name;
    private String date;
    private String id;
    private Boolean checked;

    public Data() { }

    public Data(ItemType type, String amount, String name, String date, String id, Boolean checked) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.date = date;
        this.id = id;
        this.checked = checked;
    }

    public ItemType getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getChecked() {
        return checked;
    }
}
