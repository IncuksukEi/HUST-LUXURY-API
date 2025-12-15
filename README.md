# 📌 Luxury Backend API Documentation

## 🔐 Authentication APIs (`/api/auth`)

### POST /api/auth/login

Login cho người dùng.

Request:

``` json
{
  "email": "string",
  "password": "string"
}
```

Response:

``` json
{
  "token": "jwt_token",
  "role": "CUSTOMER | ADMIN"
}
```

------------------------------------------------------------------------

### POST /api/auth/admin-login

Login cho ADMIN.

------------------------------------------------------------------------

### POST /api/auth/signup

Đăng ký tài khoản mới.

------------------------------------------------------------------------

### POST /api/auth/logout

Logout, blacklist JWT token.

------------------------------------------------------------------------

## 👤 User APIs (`/api/user`)

### GET /api/user/profile

Lấy thông tin user hiện tại.

### POST /api/user/update

Cập nhật thông tin user.

### POST /api/user/reset-password

Đổi mật khẩu.

------------------------------------------------------------------------

## 🛒 Cart APIs (`/api/cart`)

### GET /api/cart

Lấy giỏ hàng.

### POST /api/cart/add

Thêm sản phẩm vào giỏ.

### POST /api/cart/update

Cập nhật số lượng giỏ hàng.

### DELETE /api/cart/delete/{productId}

Xóa sản phẩm khỏi giỏ.

------------------------------------------------------------------------

## 📦 Product APIs (`/api/products`)

### GET /api/products/search

Tìm kiếm sản phẩm.

### GET /api/products/{id}

Chi tiết sản phẩm.

------------------------------------------------------------------------

## 📂 Category APIs (`/api/categories`)

### GET /api/categories

Lấy danh sách category.

------------------------------------------------------------------------

## 📑 Order APIs (`/api/orders`)

### POST /api/orders

Tạo đơn hàng.

### GET /api/orders

Lấy đơn hàng của user.

### PATCH /api/orders/{orderId}/status

Cập nhật trạng thái đơn hàng.

### GET /api/orders/management

Danh sách đơn cho quản lý.

------------------------------------------------------------------------

## 🧑‍💼 Admin Product APIs (`/api/admin/products`)

### GET /api/admin/products

Danh sách sản phẩm (ADMIN).

### POST /api/admin/products

Tạo sản phẩm mới.

### PUT /api/admin/products/{id}

Cập nhật sản phẩm.

### DELETE /api/admin/products/{id}

Xóa sản phẩm.

------------------------------------------------------------------------

## 🧾 Admin Order APIs (`/api/adorders`)

-   GET /api/adorders
-   GET /api/adorders/{id}
-   POST /api/adorders
-   PUT /api/adorders/{id}
-   DELETE /api/adorders/{id}

------------------------------------------------------------------------

## 📊 Analytics APIs (`/api/analytics`)

-   GET /api/analytics/unique-customers
-   GET /api/analytics/total-orders
-   GET /api/analytics/total-quantity
-   GET /api/analytics/monthly-sales
-   GET /api/analytics/monthly-customers

------------------------------------------------------------------------

## 📈 Dashboard APIs (`/api/dashboard`)

-   GET /api/dashboard/total-revenue
-   GET /api/dashboard/cancelled-orders
-   GET /api/dashboard/combo-revenue
-   GET /api/dashboard/product-sales
-   GET /api/dashboard/cancelled-rate

------------------------------------------------------------------------

## 🧂 Ingredient APIs (`/api/ingredients`)

-   GET /api/ingredients
-   GET /api/ingredients/{id}
-   POST /api/ingredients
-   PUT /api/ingredients/{id}
-   DELETE /api/ingredients/{id}
