# Test Cases

## Authentication Module

| Test ID | Description | Preconditions | Steps | Expected Result | Priority |
|---------|-------------|---------------|-------|-----------------|----------|
| TC_AUTH_001 | Successful registration | Valid details | Enter valid name, email, phone, pass | User created | High |
| TC_AUTH_002 | Registration with existing email | Email in DB | Enter existing email | Validation error | High |
| TC_AUTH_003 | Invalid email format | None | Enter invalid email | Validation error | Medium |
| TC_AUTH_004 | Weak password | None | Enter weak password | Validation error | Medium |
| TC_AUTH_005 | Successful login | Valid user in DB | Enter valid email & password | Login success | High |

## Order Management

| Test ID | Description | Preconditions | Steps | Expected Result | Priority |
|---------|-------------|---------------|-------|-----------------|----------|
| TC_ORD_001 | Add item to cart | Logged in | Add item from menu | Item in cart | High |
| TC_ORD_004 | Apply promo code | Item in cart | Apply valid code | Discount applied | Medium |
| TC_ORD_007 | Place order with COD | Items in cart | Select COD & place order | Order placed | High |
| RT_006 | Empty cart checkout | Empty cart | Attempt checkout | Blocked | High |

## Restaurant Management

| Test ID | Description | Preconditions | Steps | Expected Result | Priority |
|---------|-------------|---------------|-------|-----------------|----------|
| TC_RES_001 | Add menu item | Restaurant registered | Add item to menu | Item is visible | High |
