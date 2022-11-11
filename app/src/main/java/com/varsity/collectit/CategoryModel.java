package com.varsity.collectit;

//BTech Days (2021). 2. Retrieve Firebase data to RecyclerView. YouTube. Available at: https://www.youtube.com/watch?v=ePKC5ZEqeNY&ab_channel=BTechDays [Accessed 27 May 2022].
public class CategoryModel {
    //UserID
    //CategoryID
    private String userID;
    private String categoryName;
    private String itemGoal;

    public CategoryModel(){
    }

    public CategoryModel(String itemGoal, String categoryName, String userID){
        this.categoryName = categoryName;
        this.itemGoal = itemGoal;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getItemGoal() {
        return itemGoal;
    }

    public void setItemGoal(String itemGoal) {
        this.itemGoal = itemGoal;
    }

}
