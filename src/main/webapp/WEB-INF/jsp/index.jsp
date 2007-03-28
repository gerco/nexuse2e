<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<title>CIDX Demo - X-ioma - Large Company Role</title>
<!--
    <meta http-equiv="Refresh" content= "10;URL=">
    -->
<link rel="stylesheet" type="text/css" href="../../html/xioma.css">
</head>
<body>


<table border="0"height="100%" width="100%" style="padding:10px;cell-spacing:10px;">
	<tr>
		<td height="80px" style="background-color: white;background-image:url(../../images/xioma-logo-small.png);background-repeat:no-repeat;background-position:right;vertical-align:middle" >
<h1>CIDX Web Services - Proof of Concept</h1>
<h2>Role: Large Chemical Company</h2>
		</td>
		</tr>
	<tr>
		<td style="background-image:url(../../images/01_Picture.jpg);background-repeat:no-repeat;background-position:center;vertical-align:top;" >
		<table style="background-color: white;border:thin solid grey; border-spacing:1px; margin:10px;width:100%">
			<tr>
				<th>Purchase Order Number</th>
				<th>Shipment Number</th>
				<th>Status</th>
				<th>Set Status</th>
				<th>&nbsp;</th>
				<th>&nbsp;</th>
			</tr>
			<c:forEach items="${orderList}" var="order">
				<tr>
					<td>${order.purchaseOrderNumber}</td>
					<td>${order.shipmentNumber}</td>
					<td>${order.status}</td>
					<form action="setStatus.html">
					<td><input type="hidden" value="${order.purchaseOrderNumber}"
						name="purchaseOrderNumber" /> <select name="statusName" size="1"
						onchange="submit()">
						<c:forEach items="${statusList}" var="status">
							<option value="${status}"
								<c:if test="${status eq order.status}">selected</c:if>>${status}</option>
						</c:forEach>
					</select></td>
					</form>
					<form action="orderStatusRequest.html">
					<td><input type="hidden" value="${order.purchaseOrderNumber}"
						name="purchaseOrderNumber" /> <input type="submit"
						value="OrderStatusRequest"></td>
					</form>
					<form action="shipmentStatusRequest.html">
					<td><input type="hidden" value="${order.shipmentNumber}"
						name="shipmentNumber" /> <input type="submit"
						value="ShipmentStatusRequest"></td>
					</form>
				</tr>
			</c:forEach>
		</table>
		</td>
	</tr>
</table>

</body>
</html>
