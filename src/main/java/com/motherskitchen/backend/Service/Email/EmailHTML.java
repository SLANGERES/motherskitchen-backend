package com.motherskitchen.backend.Service.Email;

import com.motherskitchen.backend.DTO.Order.OrderItemDTO;
import com.motherskitchen.backend.DTO.Order.OrdersDTO;
import com.motherskitchen.backend.Models.Address;

import java.time.format.DateTimeFormatter;

public class EmailHTML {

    public static String accountCreation(String name, String email, String accountId, String link) {

        String template = """
                <table style="background-color: #f4f4f4; padding: 20px;" width="100%%" cellspacing="0" cellpadding="0">
                    <tbody>
                        <tr>
                            <td align="center">
                                <table style="background-color: #ffffff; border-radius: 8px; overflow: hidden; 
                                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);" width="600" cellspacing="0" cellpadding="0">
                                    
                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                                                padding: 40px; text-align: center;">
                                            <h1 style="color: #ffffff; margin: 0; font-size: 28px;">Welcome Aboard! 🎉</h1>
                                        </td>
                                    </tr>

                                    <!-- Content -->
                                    <tr>
                                        <td style="padding: 40px 30px;">
                                            <h2 style="color: #333333; margin: 0 0 20px 0; font-size: 24px;">
                                                Hi %s,
                                            </h2>

                                            <p style="color: #666666; line-height: 1.6; font-size: 16px; margin: 0 0 20px 0;">
                                                Your account has been successfully created! 
                                                We're thrilled to have you join Mothers Kitchen.
                                            </p>

                                            <!-- Account Details Box -->
                                            <div style="background-color: #f8f9fa; border-left: 4px solid #667eea; 
                                                    padding: 20px; margin: 0 0 30px 0; border-radius: 4px;">
                                                <p style="margin: 0 0 10px 0; color: #333333; font-weight: bold;">
                                                    Account Details:
                                                </p>

                                                <p style="margin: 0 0 5px 0; color: #666666;">
                                                    <strong>Email:</strong> %s
                                                </p>

                                                <p style="margin: 0; color: #666666;">
                                                    <strong>Account ID:</strong> %s
                                                </p>
                                            </div>

                                            <!-- CTA Button -->
                                            <table width="100%%" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td style="padding: 10px 0 30px 0;" align="center">
                                                        <a href="%s" 
                                                        style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                                                               color: #ffffff; padding: 15px 40px; text-decoration: none; 
                                                               border-radius: 5px; font-weight: bold; display: inline-block; 
                                                               font-size: 16px;">Get Started</a>
                                                    </td>
                                                </tr>
                                            </table>

                                            <p style="color: #666666; line-height: 1.6; font-size: 14px; margin: 0;">
                                                If you have any questions, reach out to our support team at
                                                <a href="mailto:support@motherskitchen.se" style="color: #667eea; text-decoration: none;">
                                                    support@motherskitchen.se
                                                </a>
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color: #f8f9fa; padding: 30px; text-align: center;
                                                border-top: 1px solid #e0e0e0;">
                                            <p style="color: #999999; font-size: 14px; margin: 0 0 10px 0;">
                                                © 2025 Mothers Kitchen. All rights reserved.
                                            </p>

                                            <p style="color: #999999; font-size: 12px; margin: 0;">
                                                <a href="#" style="color: #667eea; text-decoration: none; margin: 0 10px;">Unsubscribe</a> |
                                                <a href="#" style="color: #667eea; text-decoration: none; margin: 0 10px;">Privacy Policy</a>
                                            </p>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </tbody>
                </table>
                """;
        return String.format(template, name, email, accountId, link);
    }
    public static String orderConfirmation(OrdersDTO order, String customerName) {

        String orderDate = order.getDeliveryDate() != null
                ? order.getDeliveryDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                : "N/A";

        StringBuilder itemsHtml = getStringBuilder(order);

        Address address = order.getAddress();

        String template = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Order Confirmation - Mother's Kitchen</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
            
            <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                <tr>
                    <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden;">
                            
                            <!-- Header -->
                            <tr>
                                <td style="background: linear-gradient(135deg, #ff6b6b 0%%, #ee5a24 100%%); padding: 40px; text-align: center;">
                                    <div style="font-size: 48px; margin-bottom: 10px;">🍽️</div>
                                    <h1 style="color: #ffffff; margin: 0; font-size: 32px;">Mother's Kitchen</h1>
                                    <p style="color: #ffffff; margin: 15px 0 0; font-size: 18px;">Order Confirmed!</p>
                                </td>
                            </tr>
                            
                            <!-- Content -->
                            <tr>
                                <td style="padding: 40px 30px;">
                                    <h2 style="color: #333333; margin: 0 0 10px; font-size: 24px;">Hi %s,</h2>
                                    <p style="color: #666666; line-height: 1.6; font-size: 16px; margin-bottom: 30px;">
                                        Thank you for ordering from Mother's Kitchen! Your meal is being prepared fresh and will be delivered soon.
                                    </p>
            
                                    <!-- Order Summary -->
                                    <div style="background: #fff3f3; border-left: 4px solid #ff6b6b; padding: 25px; margin-bottom: 30px; border-radius: 8px;">
                                        <h3 style="color: #333333; margin-bottom: 20px;">Order Summary</h3>
                                        <table width="100%%" cellpadding="5" cellspacing="0">
                                            <tr>
                                                <td><strong>Order Number:</strong></td>
                                                <td style="text-align: right; color: #ff6b6b; font-weight: bold;">#%s</td>
                                            </tr>
                                            <tr>
                                                <td><strong>Order Date:</strong></td>
                                                <td style="text-align: right;">%s</td>
                                            </tr>
                                            <tr>
                                                <td><strong>Total Amount:</strong></td>
                                                <td style="text-align: right; color: #ff6b6b; font-weight: bold;">%.2f kr</td>
                                            </tr>
                                        </table>
                                    </div>
            
                                    <!-- Items -->
                                    <h3 style="color: #333333; margin-bottom: 20px;">Your Order</h3>
            
                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                        %s
                                    </table>
            
                                    <!-- Address -->
                                    <h3 style="margin: 20px 0 10px;">Delivery Address</h3>
                                    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px;">
                                        <p style="margin: 0; color: #666666; line-height: 1.6; font-size: 14px;">
                                            %s<br>
                                            %s<br>
                                            %s - %s<br>
                                        </p>
                                    </div>
            
                                    <br><br>
            
                                    <p style="text-align:center; font-size:14px; color:#888;">
                                        © 2024 Mother's Kitchen. All rights reserved.
                                    </p>
            
                                </td>
                            </tr>
            
                        </table>
                    </td>
                </tr>
            </table>
            
            </body>
            </html>
            """;

        return String.format(
                template,
                customerName,
                order.getId().toString(),
                orderDate,
                order.getTotalAmount(),
                itemsHtml.toString(),
                address.getStreetAddress(),
                address.getCity(),
                address.getPostalcode(),
                address.getCity()

        );
    }

    private static StringBuilder getStringBuilder(OrdersDTO order) {
        StringBuilder itemsHtml = new StringBuilder();

        for (OrderItemDTO item : order.getItems()) {

            itemsHtml.append(String.format("""
            <tr>
                <td style="padding: 20px 0; border-bottom: 1px solid #e0e0e0;">
                    <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td style="vertical-align: top;">
                                <p style="margin: 0 0 5px 0; color: #333333; font-weight: bold; font-size: 16px;">%s</p>
                                <p style="margin: 0; color: #666666; font-size: 14px;">Qty: %d</p>
                            </td>
                            <td width="100" style="text-align: right; vertical-align: top;">
                                <p style="margin: 0; color: #333333; font-weight: bold; font-size: 16px;">%.2fkr</p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            """,
                    item.getName(),
                    item.getQuantity(),
                    item.getPrice()
            ));
        }
        return itemsHtml;
    }
    public static String partyOrderEmail(String name, String email, String phone, String date,
                                         String guests, String combo, String message) {

        String template = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>Party Order Inquiry</title>
        </head>

        <body style="margin:0; padding:0; background:#f8f8f8; font-family:Arial, sans-serif;">
        
        <table width="100%%" cellspacing="0" cellpadding="0" style="padding:20px;">
            <tr>
                <td align="center">
                
                    <table width="600" cellspacing="0" cellpadding="0"
                           style="background:#fff; border-radius:10px; overflow:hidden; border:1px solid #eee;">
                        
                        <!-- HEADER -->
                        <tr>
                            <td style="background:#e17055; padding:35px; text-align:center;">
                                <div style="font-size:45px;">🎉</div>
                                <h1 style="color:#fff; margin:10px 0 0; font-size:28px;">
                                    New Party Order Inquiry
                                </h1>
                                <p style="color:#fff; opacity:0.9; margin-top:8px;">
                                    Mother's Kitchen – Party Catering
                                </p>
                            </td>
                        </tr>
                        
                        <!-- BODY -->
                        <tr>
                            <td style="padding:35px;">
                                
                                <h2 style="font-size:22px; margin-bottom:10px; color:#333;">
                                    Hello Team,
                                </h2>
                                <p style="font-size:15px; color:#555; line-height:1.6;">
                                    A customer has submitted a new party order inquiry.  
                                    Here are the full details:
                                </p>

                                <!-- USER DETAILS -->
                                <div style="background:#fff7e6; padding:20px; border-left:4px solid #ffa94d; border-radius:8px; margin:25px 0;">
                                    <h3 style="margin:0 0 15px; color:#333;">Customer Details</h3>
                                    
                                    <table width="100%%" cellpadding="6" style="font-size:15px;">
                                        <tr>
                                            <td><strong>Name:</strong></td>
                                            <td align="right">%s</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Email:</strong></td>
                                            <td align="right">%s</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Phone:</strong></td>
                                            <td align="right">%s</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Party Date:</strong></td>
                                            <td align="right">%s</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Number of Guests:</strong></td>
                                            <td align="right">%s people</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Selected Combo:</strong></td>
                                            <td align="right">%s</td>
                                        </tr>
                                    </table>
                                </div>

                                <!-- MESSAGE -->
                                <h3 style="color:#333; margin-bottom:10px;">Customer Message</h3>
                                <div style="background:#f2f2f2; padding:20px; border-radius:8px;">
                                    <p style="margin:0; font-size:14px; color:#555; line-height:1.6;">
                                        %s
                                    </p>
                                </div>

                                <p style="margin-top:25px; font-size:13px; color:#999; text-align:center;">
                                    © 2025 Mother's Kitchen Party Catering. All rights reserved.
                                </p>

                            </td>
                        </tr>

                    </table>

                </td>
            </tr>
        </table>

        </body>
        </html>
        """;

        return String.format(
                template,
                name,
                email,
                phone,
                date,
                guests,
                combo,
                (message == null || message.isBlank()) ? "No additional message." : message
        );
    }



}

