package com.swiftdroid.posterhouse.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity(name = "Product")
@Table(name = "mst_product")
public class Product implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	
	@Column(name = "pc")
	private String productCode;
	
	
	
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="category_id")
	private ProductType productType;
	
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product",fetch = FetchType.EAGER)
	private List<ProductConfig> productConfig;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	@JsonIgnore
	private List<ProductToCartItem> ProductToCartItemList;
	
	
	@Column(name = "pn")
	private String productName;
	
	@Column(name = "pd",columnDefinition="text")
	private String productDescription;
	
	@Column(name = "nf")
	private int noOfFaces;
	
	@Column(name = "QT")
	private int maximumQuantity;
	
	
	@Column(name = "ia")
	private boolean isEnable=true;

    @Transient
	private MultipartFile bookImage;

    @Column(name = "CD")
    @CreationTimestamp
	private Date cretedDate;

	@Column(name = "CB", updatable = false)
	private String cretedBy;

	@Column(name = "MD")
	@UpdateTimestamp
	private Date modifiedDate;
	
	@Column(name = "MB")
	private String modifiedBy;

	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public List<ProductConfig> getProductConfig() {
		return productConfig;
	}

	public void setProductConfig(List<ProductConfig> productConfig) {
		this.productConfig = productConfig;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public int getNoOfFaces() {
		return noOfFaces;
	}

	public void setNoOfFaces(int noOfFaces) {
		this.noOfFaces = noOfFaces;
	}

	public int getMaximumQuantity() {
		return maximumQuantity;
	}

	public void setMaximumQuantity(int maximumQuantity) {
		this.maximumQuantity = maximumQuantity;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

	public MultipartFile getBookImage() {
		return bookImage;
	}

	public void setBookImage(MultipartFile bookImage) {
		this.bookImage = bookImage;
	}

	public Date getCretedDate() {
		return cretedDate;
	}

	public void setCretedDate(Date cretedDate) {
		this.cretedDate = cretedDate;
	}

	public String getCretedBy() {
		return cretedBy;
	}

	public void setCretedBy(String cretedBy) {
		this.cretedBy = cretedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
public List<ProductToCartItem> getProductToCartItemList() {
		return ProductToCartItemList;
	}
      
	public void setProductToCartItemList(List<ProductToCartItem> productToCartItemList) {
		ProductToCartItemList = productToCartItemList;
	}

	
	
	
	
}
