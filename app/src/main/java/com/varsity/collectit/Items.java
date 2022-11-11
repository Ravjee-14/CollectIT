package com.varsity.collectit;

public class Items {
    private String UserID;
    private String categoryID;
    private String itemName;
    private String description;
    private String dateofAcq;
    private String cost;
    private String category;
    private String imageURL;

    public Items(){

    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    //getter and setters for Item Name
    public String getItemName(){
        return itemName;
    }

    public void setItemName(String itemName){
        this.itemName = itemName;
    }

    //getter and setters for Description
    public String getDescription(){
        return  description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    //getter and setters for Date of Acquisition
    public String getDateofAcq(){
        return dateofAcq;
    }

    public void setDateofAcq(String dateofAcq){
        this.dateofAcq = dateofAcq;
    }

    //getter and setters for Date of Acquisition
    public String getCost(){
        return cost;
    }

    public void setCost(String cost){
        this.cost = cost;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = this.category;
    }

    public String getImageURL(){
        return imageURL;
    }

    public void setImageURL(String imageURL){
        this.imageURL = imageURL;
    }
}