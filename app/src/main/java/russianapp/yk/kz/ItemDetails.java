package russianapp.yk.kz;

import android.graphics.drawable.Drawable;

public class ItemDetails {
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}	
	
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}	
	
	public Drawable getImage() {
		return imageNumber;
	}
	public void setImage(Drawable imageNumber) {
		this.imageNumber = imageNumber;
	}
	
	public String getFullImage() {
		return imageFullNumber;
	}
	public void setFullImage(String imageFullNumber) {
		this.imageFullNumber = imageFullNumber;
	}
	
	private String category;
	private String id;
	private String itemDescription;
	private String price;
	private String phone;
	private String date;
	private Drawable imageNumber;
	private String imageFullNumber;
	
}
