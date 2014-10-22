<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<xsl:param name="currency_symbol"/>
<xsl:param name="currency_exchange_rate"/>
<xsl:param name="is_left_symbol"/>
<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<style type="text/css">
body {
	background-color: #FFFFFF;
	margin-left: 0px;
	margin-top: 0px;
	background-repeat: no-repeat;
	font-size: 16px;
	color: #666;
}
a {
	color: #666;
	font-weight: bold;
}
a:link {
	text-decoration: none;
}
a:visited {
	text-decoration: none;
	color: #FFF;
}
a:hover { 
	color: #00F;
}
a:active {
	text-decoration: none;
	text-decoration: none;
	color: #FFF;
}
h1 {
	font-size: 20px;
	color: #55692F;
	font-weight: bold;
	font-family: Verdana, Geneva, sans-serif;
}
.td {
	color: #405D94;
	font-size: 16px;
}
.line {
    border-bottom:  dotted 1px   #CCC;
}
</style>
<script type='text/javascript' >
<xsl:comment>     
	 var limitlength=function(str){
    	if(str.length>5){
    	    document.getElementById('qty').value=str.substring(0,5);
    	}   
	 };
     var addtoquote = function(){     
        android.addToQuote(document.getElementById('qty').value);
     };
     var findAndShowPicture=function(imgUrl,filename){
        android.findOrDownloadImage(imgUrl,filename);
     };
     var showImage=function(filename){
     	//alert(filename);
     	document.getElementById('image').src="file:///data/data/com.koobest.m.supermarket.activities/files/"+filename;
     };
     
   //  
     
//</xsl:comment>	     
</script>
</head>
<body>
<!-- asd -->
<table width="100%" height="779" border="0.1">
  <tr>
    <td height="45" colspan="2"><h1><xsl:apply-templates select="product/name" /></h1></td>
  </tr>
  <xsl:apply-templates select="product"/>
</table>   <!-- /asd -->
</body>
</html>
</xsl:template>

<xsl:template match="images">
  <xsl:for-each select="image">
               <img height="180px" width="150px" id="image"/>
<script type='text/javascript' >
<xsl:comment>
var imgUrl,filename;
imgUrl=<![CDATA[']]><xsl:value-of select="."></xsl:value-of><![CDATA[']]>;
filename=<![CDATA[']]><xsl:value-of select="../../product_id"></xsl:value-of><xsl:value-of select="count(preceding-sibling::*[name()=name(current())])" />.png<![CDATA[']]>;
document.onload=findAndShowPicture(imgUrl,filename);
//</xsl:comment>   
</script>     
  </xsl:for-each>
</xsl:template>

<xsl:template match="product">
<!-- asdfg -->
<xsl:variable name="price"><xsl:value-of select='translate(price, ",", "")'/></xsl:variable>
 <tr>
     <td height="1" colspan="2" class="line" ></td>
 </tr>
<tr>
    <td width="150" height="180" valign="top"><xsl:apply-templates select="images"/></td>
<td valign="middle">
    <table width="100%" border="0">
      <tr>
        <td width="32%"  class="td">价格</td>
           <xsl:choose>
         	  <xsl:when test="$is_left_symbol">
                   <td class="price_type"><xsl:value-of select="$currency_symbol"/><xsl:value-of select='format-number($price*$currency_exchange_rate,"###,##0.00")'/></td>
              </xsl:when> 
              <xsl:otherwise>
                   <td class="price_type"><xsl:value-of select='format-number($price*$currency_exchange_rate,"###,##0.00")'/><xsl:value-of select="$currency_symbol"/></td>
              </xsl:otherwise> 
           </xsl:choose>
      </tr>
      
      <tr>
        <td height="1" colspan="2" class="line" ></td>
        </tr>
      <tr>
          <td height="30" class="td">库存</td>
          <td><xsl:value-of select="stock_status/name" /></td>
      </tr>
      <tr>
        <td height="1" colspan="2" class="line"></td>
        </tr>
      <tr>
        <td height="30" valign="middle" class="td">品牌</td>
        <td><xsl:value-of select="brand/name"/></td>
      </tr>     
      <tr valign="middle">
        <td height="1" colspan="2" class="line"></td>
      </tr>
    </table>
    
     
</td>
  </tr>
  <tr>
    <td colspan="2" valign="top">
<table width="100%" border="0">
        <xsl:for-each select="product_barcodes/product_barcode">  
         <tr>
            <td height="30" width="30%" valign="middle" class="td">条形码</td>
            <td><xsl:value-of select="barcode" /></td>
         </tr>
         <tr valign="middle">
           <td height="1" colspan="2" class="line"></td>
         </tr>
         </xsl:for-each>
        <tr>
          <td height="30" valign="middle" class="td">型号</td>
          <td><xsl:value-of select="model" /></td>
        </tr>       
        <tr valign="middle">
          <td height="1" colspan="2" class="line"></td>
        </tr>
        <tr>
          <td height="30" valign="middle" class="td">重量</td>
          <td><xsl:value-of select="format-number(weight, '###,##0.0')" /><xsl:value-of select="weight_class/unit" /></td>
        </tr>
        <tr valign="middle">
          <td height="1" colspan="2" class="line"></td>
        </tr>
        <tr>
          <td width="20%"  height="30" valign="middle" class="td">体积</td>
          <td width="80%"><xsl:value-of select='format-number(length,"###,##0")' /><xsl:value-of select="length_class/unit" />*<xsl:value-of select='format-number(width,"###,##0")' /><xsl:value-of select="length_class/unit" />*<xsl:value-of select='format-number(height,"###,##0")' /><xsl:value-of select="length_class/unit" /></td>
        </tr>
        <tr valign="middle">
          <td height="1" colspan="2" class="line"></td>
        </tr>
        
        <tr>
          <td height="30" valign="middle" class="td">规格</td>
          <td><xsl:value-of select="description/h3" /><xsl:value-of select="description/p"/></td>
        </tr>
        <tr valign="middle">
          <td height="1" colspan="2" class="line"></td>
        </tr>
        <tr>
          <td height="30" valign="middle" class="td">港口</td>
          <td><xsl:value-of select="port/name"/></td>
        </tr>
        <tr valign="middle">
          <td height="1" colspan="2" class="line"></td>
        </tr>
        <tr>
          <td height="30" valign="middle" class="td">区域</td>
          <td><xsl:value-of select="zone/name"/></td>
        </tr>
        <tr valign="middle">
          <td height="1" colspan="2"  class="line"></td>
        </tr>
        <tr>
          <td height="30" class="td">制造商</td>
          <td><xsl:value-of select="manufacturer/name"/></td>
        </tr>
     <!--  <tr>
        <td height="1" colspan="2"  class="line"></td>
        </tr>
        <tr>
          <td height="30" valign="middle" class="td">有效日期</td>
          <td><xsl:value-of select="date_available" /></td>
        </tr> -->
        <tr valign="middle">
          <td height="1" colspan="2" class="line"></td>
        </tr>
      </table>
    </td> 
 </tr><!-- /asdfg -->
</xsl:template>

</xsl:stylesheet>


