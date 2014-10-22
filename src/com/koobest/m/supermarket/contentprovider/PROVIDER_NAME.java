package com.koobest.m.supermarket.contentprovider;

import java.util.HashMap;

import com.koobest.m.supermarket.database.NAME;

import android.net.Uri;

public class PROVIDER_NAME extends NAME{
	public static final String AUTHORITY = "com.koobest.m.supermarket.contentprovider";
	
	
	//product detail
	public static final Uri PRODUCT_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_PRODUCT);
	public static final Uri PRODUCT_PRICE_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_PRODUCT_PRICE);
	public static final Uri PRODUCT_IMAGE_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_PRODUCT_IMAGE);
	//product search
	public static final Uri BARCODE_PRODUCT_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_BARCODE_PRODUCT);
	public static final Uri PRODUCT_DESC_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_PRODUCT_DESC);
	//wishList
	public static final Uri WISHLIST_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_WISHLIST);
	public static final Uri WISHILST_PRODUCT_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_WISHLIST_PRODUCT);
    //quote
	public static final Uri QUOTE_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_QUOTES);
	public static final Uri QUOTE_PRODUCT_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_QUOTE_PRODUCT);
	//order
	public static final Uri ORDER_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_ORDERS);
	
	
	


	public static HashMap<String, String> sNotesProjectionMap;
	static {
		sNotesProjectionMap = new HashMap<String, String>();
		sNotesProjectionMap.put(PRODUCT_ID, "product_id");
		sNotesProjectionMap.put(PRODUCT_XML, "xml");
		sNotesProjectionMap.put(PRODUCT_IMAGE_ID, "product_image_id");
		sNotesProjectionMap.put(IMAGE, "image");
		sNotesProjectionMap.put(WISHLIST_ID, "wishlist_id");
		sNotesProjectionMap.put(CUSTOMER_ID, "customer_id");
		sNotesProjectionMap.put(ORDER_ID, "order_id");
		sNotesProjectionMap.put(STATUS, "status");
		sNotesProjectionMap.put(ORDER_DATE, "order_date");
		sNotesProjectionMap.put(ORDER_XML, "order_xml");
		//sNotesProjectionMap.put(BARCODE_ID, "barcode_id");
		//sNotesProjectionMap.put(WISH_PRODUCT_ID, "wish_product_id");
		sNotesProjectionMap.put(PRODUCT_NAME, "product_name");
		sNotesProjectionMap.put(DESCRIPTION, "description");
	}
	
	
	//sync order list
	public static final String AUTHORITY_ORDERS = "com.koobest.sync.orders";
	public static final Uri SYNC_ORDERLIST_CONTENT_URI=Uri.parse("content://"+AUTHORITY_ORDERS+"/"+TABLE_ORDERS);
	//sync order list
	public static final String AUTHORITY_PRICE = "com.koobest.sync.product.price";
	public static final Uri SYNC_PRO_PRICE_CONTENT_URI=Uri.parse("content://"+AUTHORITY_PRICE+"/"+TABLE_PRODUCT_PRICE);
	//sync template orders
	public static final String AUTHORITY_TEMP_ORDERS = "com.koobest.sync.temp_orders";
	public static final Uri SYNC_TEMPORDER_FACETS_CONTENT_URI=Uri.parse("content://"+AUTHORITY_TEMP_ORDERS+"/"+TABLE_TEMP_ORDER_FACETS);
	public static final Uri SYNC_TEMP_ORDERS_CONTENT_URI=Uri.parse("content://"+AUTHORITY_TEMP_ORDERS+"/"+TABLE_TEMP_ORDERS);
	public static final String TEMP_ORDER_ID = "temp_order_id";

}
