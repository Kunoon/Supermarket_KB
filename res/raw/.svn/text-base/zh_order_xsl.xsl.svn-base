<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<style type="text/css">
					body,td,th {
					color: #333;
					font-size: 16px;
					}
					.b {
					color: #306590;
					font-size: 15px;
					}
					.n {
					color: #65769A;
					}
					.bb {
					color: #FFFFFF;
					font-size: 17px;
					}
					.bi {
					font-size: 15px;
					}
					.td {
					font-size: 17px;
					color:#595150
					}
					.th {
					font-size: 14px;
					}
					.line {
					border-bottom: solid 1px #C6D3DF;
					}
					.td{
					height:30px}
					.singleLine{
					background-color:#FFFFFF}
					.doubleLine{
					background-color:#EEEDE9}
				</style>
			</head>
			<body background="#D9FFFF">
				<xsl:apply-templates select="order"></xsl:apply-templates>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="order">
		<!-- aa -->
		<table width="100%" cellpadding="0px" cellspacing="0px" style="border: solid 1px #C6D3DF;">
			<xsl:choose>
				<xsl:when test="order_status_id&lt;200 and order_status_id&gt;99">
					<tr height="33" bgcolor="#434F6C">
						<td width="10%" align="left" class="bb">报价单号</td>
						<td width="90%" align="center" class="bb">
							<xsl:value-of select="order_id" />
						</td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr height="33" bgcolor="#434F6C">
						<td width="10%" align="left" class="bb">订单号</td>
						<td width="90%" align="center" class="bb">
							<xsl:value-of select="order_id" />
						</td>
					</tr>
				</xsl:otherwise>
			</xsl:choose>
			<tr border="0px" cellpadding="0px">
				<td colspan="2">
					<table width="100%" border="0px" cellspacing="0px" cellpadding="0px">
						<tr>
							<td height="35" colspan="2" valign="middle">
								<span class="td"> 商品清单</span>
							</td>
						</tr>
						<tr>
							<td height="1" colspan="2" class="line"></td>
						</tr>
						<tr>
							<td bgcolor="#CAD9E6">
								<table width="100%" cellspacing="0" style="border: solid 0px #C6D3DF;">
									<xsl:apply-templates select="product_list"></xsl:apply-templates>
								</table>
							</td>
						</tr>
						<tr>
							<td height="1" colspan="2" class="line"></td>
						</tr>
						<tr>
							<td height="35" colspan="2" valign="middle">
								<span class="td"> 信　　息</span>
							</td>
						</tr>
						<tr>
							<td height="296" colspan="2" valign="top" bgcolor="#CAD9E6">
								<table width="100%" cellspacing="1" style="border: solid 0px #C6D3DF;">
									<!--
										<tr> <td width="35%" height="33" align="center" valign="bottom"
										bgcolor="#F9F6EE" class="bi">订单号</td> <td width="65%"
										bgcolor="#FFFFFF"><xsl:value-of select="order_id" /></td> </tr>
									-->
									<tr bgcolor="#EEEDE9">
										<td height="33" width="35%" align="left">状态</td>
										<td width="65%">
											<xsl:value-of select="order_status" />
										</td>
									</tr>
									<tr bgcolor="#FFFFFF">
										<td height="33" align="left">电话</td>
										<td>
											<xsl:value-of select="telephone" />
										</td>
									</tr>
									<tr bgcolor="#EEEDE9">
										<td height="33" width="35%" align="left">邮箱地址</td>
										<td>
											<xsl:value-of select="email" />
										</td>
									</tr>
									<tr bgcolor="#FFFFFF">
										<td height="33" align="left">配送方式</td>
										<td>
											<xsl:value-of select="shipping_method" />
										</td>
									</tr>
									<tr bgcolor="#EEEDE9">
										<td height="33" align="left">支付方式</td>
										<td>
											<xsl:value-of select="payment_method" />
										</td>
									</tr>
									<tr bgcolor="#FFFFFF">
										<td height="33" align="left">支付帐期</td>
										<td>
											定金
											<xsl:value-of select="payment_term/deposit" />
											%；帐期
											<xsl:value-of select="payment_term/grace_period" />
										</td>
									</tr>
									<tr bgcolor="#EEEDE9">
										<td height="33" align="left">配送地址</td>
										<td>
											<xsl:value-of select="shipping_address/address_1" />
											,
											<xsl:value-of select="shipping_address/city" />
										</td>
									</tr>
									<tr bgcolor="#FFFFFF">
										<td height="33" align="left">付款地址</td>
										<td>
											<xsl:value-of select="payment_address/address_1" />
											,
											<xsl:value-of select="payment_address/city" />
										</td>
									</tr>
									<tr bgcolor="#EEEDE9">
										<td height="33" align="left">传真</td>
										<td>
											<xsl:value-of select="fax" />
										</td>
									</tr>
									<tr bgcolor="#FFFFFF">
										<td height="33" align="left">联票编号</td>
										<td>
											<xsl:value-of select="invoice_id" />
										</td>
									</tr>
									<tr bgcolor="#EEEDE9">
										<td height="33" align="left">描述</td>
										<td>
											<xsl:value-of select="description" />
										</td>
									</tr>
									<!--
										<tr> <td height="33" align="center" bgcolor="#F2F2F2"
										class="bi">备注</td> <td bgcolor="#F2F2F2"><xsl:value-of
										select="history_list/history/comment" /></td> </tr>
									-->
									<tr bgcolor="#FFFFFF">
										<td height="33" align="left">添加日期</td>
										<td>
											<xsl:value-of select="history_list/history/date_added" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<!--
							<tr> <td colspan="2" valign="middle" bgcolor="#CAD9E6"> <span
							class="td"> 商品清单</span></td> </tr> <tr> <td height="110" colspan="2"
							bgcolor="#CAD9E6"> <table width="100%"
							style="padding-left:2px;padding-top:2px;padding-right:2px;"
							cellspacing="0" > <xsl:apply-templates
							select="product_list"></xsl:apply-templates> </table> </td> </tr>
						-->
						<tr>
							<td height="35" colspan="2" valign="middle">
								<span class="td"> 总　　计</span>
							</td>
						</tr>
						<tr>
							<td width="100%" colspan="2" bgcolor="#CAD9E6">
								<table width="100%" cellspacing="1px" cellpadding="0px">
									<xsl:apply-templates select="total_list"></xsl:apply-templates>
								</table>
							</td>
						</tr>
						<tr>
							<td height="35" colspan="2" valign="middle">
								<span class="td">历史信息</span>
							</td>
						</tr>
						<tr>
							<td width="100%" colspan="2" bgcolor="#CAD9E6">
								<table width="100%" cellspacing="1px" style="border: solid 0px #C6D3DF;" cellpadding="0px">
									<xsl:apply-templates select="history_list"></xsl:apply-templates>
								</table>
							</td>
						</tr>
					</table><!-- /aa -->
				</td>
			</tr>
		</table><!-- /aa -->
	</xsl:template>
	
	<xsl:template match="history_list">
		<xsl:for-each select="history">
			<tr class="singleLine">
				<td height="33" width="35%" align="left">订单时间</td>
				<td width="65%">
					<xsl:value-of select="date_added" />
				</td>
			</tr>
			<tr class="doubleLine">
				<td height="33" align="left">描述</td>
				<td>
					<xsl:value-of select="comment" />
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="product_list">
		<xsl:for-each select="product">
			<xsl:variable name="current"
				select="count(preceding-sibling::*[name()=name(current())])"></xsl:variable>
			<xsl:choose>
				<xsl:when test="$current mod 2=1">
					<tr class="singleLine">
						<td colspan="6" class="n" align="left">
							<xsl:value-of select="name" />
						</td>
					</tr>
					<tr class="singleLine">
						<td width="30%" align="left" valign="bottom" class="th">
							单价:
							<xsl:value-of select='price'/>
						</td>
						<td width="3%" align="center" valign="bottom" class="th">
							<img src="file:///android_asset/order_divide.png" width="1" height="15"/>
						</td>
						<td width="28%" align="left" valign="bottom" class="th">
							数量:
							<xsl:value-of select="quantity"/>
						</td>
						<td width="3%" align="center" valign="bottom" class="th">
							<img src="file:///android_asset/order_divide.png" width="1" height="15"/>
						</td>
						<td width="32%" align="left" valign="bottom" class="th">
							总价:
							<xsl:value-of select='total'/>
						</td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr class="doubleLine">
						<td colspan="6" class="n" align="left">
							<xsl:value-of select="name" />
						</td>
					</tr>
					<tr class="doubleLine">
						<td width="30%" align="left" valign="bottom" class="th">
							单价:
							<xsl:value-of select='price'/>
						</td>
						<td width="3%" align="center" valign="bottom" class="th">
							<img src="file:///android_asset/order_divide.png" width="1" height="15"/>
						</td>
						<td width="28%" align="left" valign="bottom" class="th">
							数量:
							<xsl:value-of select="quantity"/>
						</td>
						<td width="3%" align="center" valign="bottom" class="th">
							<img src="file:///android_asset/order_divide.png" width="1" height="15"/>
						</td>
						<td width="32%" align="left" valign="bottom" class="th">
							总价:
							<xsl:value-of select='total'/>
						</td>
					</tr>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="total_list">
		<xsl:for-each select="total">
			<xsl:variable name="current1"
				select="count(preceding-sibling::*[name()=name(current())])"></xsl:variable>
			<xsl:choose>
				<xsl:when test="$current1 mod 2=1">
					<tr height="33">
						<td width="35%" class="singleLine">
							<xsl:value-of select="title" />
						</td>
						<td width="65%" class="singleLine">
							<xsl:value-of select="text" />
						</td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr height="33">
						<td width="35%" class="doubleLine">
							<xsl:value-of select="title" />
						</td>
						<td width="65%" class="doubleLine">
							<xsl:value-of select="text" />
						</td>
					</tr>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>

