Collaborated with Yi Fang to create a e-commerce movie database website Fabflix (parody of Netflix).

Technologies used:

Java, MySQL (JDBC), JSP, Java Servlet, HTML, XML, Amazon Web Services (AWS), Apache Tomcat, Google Recaptcha

In the website the user can do a secure login using an email, password, and recaptcha for authentication. From there the user can either search or browse for movies on the home page.

With the search page, the user can search for movies by one or more conditions: given the movie's title, year, director, and actors/actresses. With the browsing page, the user can browse via movie genre or movie title.

Once the requests are made, the website sends requests to retrieve information from the MySQL database and the website generates a movie list that displays the movies and their information (title, year, director, genres, actors/actresses, and rating).

The Fabflix website also supports a shopping cart and checkout feature. The user can add and remove movies from their shopping cart and proceed to checkout.




Fabflix User Guide

Introduction: Fabflix is an e-commerce website for searching, browsing, and buying movies. The user, after singing up can go through a collection of movie database to search by many different categories including genre, stars, title.

Purpose: To guide the users through Fabflix to search and purchase the movie the user wants.

Instructions: 
1.	Sign up for Fabflix using your own set of email and password. (Not implemented yet)
2.	Enter the email and password you have just created (ex: email: a@email.com pw: a2)
3.	Click on the reCAPTCHA box to ensure our website that you are not a bot.
4.	Click Submit.


![image](https://user-images.githubusercontent.com/68848170/118177422-85e31080-b3e7-11eb-850a-c944f2759f1a.png)

 
5.	After submit button, it will lead to the home page which will look like the image below
 
 ![image](https://user-images.githubusercontent.com/68848170/118177462-92676900-b3e7-11eb-91ed-a794bbc0e290.png)



•	Browsing by Genre
1.	Click on “Browse by Genre” button.
2.	Click on any of the genre list that you want to pick the movie out of.
3.	Click on “Title V” to sort alphabetically by title in the genre you’ve selected and “Title ^” to sort in reverse alphabetically.
4.	Click on “Rating V” to sort by rating (worst to best) and “Rating ^” for rating (best to worst).
5.	If you see a movie that you would like to purchase, Click “Add to Cart”. If you want to see more details about the movie, click on the movie title.
6.	Click on any other genre to browse by the genre that you click.
7.	Click on any star to browse by the star that you click.

![image](https://user-images.githubusercontent.com/68848170/118177471-95625980-b3e7-11eb-91a2-b77e22b81f83.png)
 

•	Browsing by Title
1.	Click on “Browse by Title” button
2.	Click on any of the list that you want to pick the movie out of.
3.	Click on “Title V” to sort alphabetically by title in the genre you’ve selected and “Title ^” to sort in reverse alphabetically.
4.	Click on “Rating V” to sort by rating (worst to best) and “Rating ^” for rating (best to worst).
5.	If you see a movie that you would like to purchase, Click “Add to Cart”. If you want to see more details about the movie, click on the movie title.
6.	Click on any other genre to browse by the genre that you click.
7.	Click on any star to browse by the star that you click.

 
![image](https://user-images.githubusercontent.com/68848170/118177481-985d4a00-b3e7-11eb-911e-866da87ee160.png)





•	Advanced Searching
1.	Click on the magnifying glass button.
2.	Fill in at least one of the categories: title, year, director, star’s name.
3.	Click on “Submit”

 ![image](https://user-images.githubusercontent.com/68848170/118177488-9bf0d100-b3e7-11eb-968d-b2440f9a0505.png)



•	Shopping Cart
1.	Click on the cart button to see the movies you have added to the buy list.
2.	Update quantity by either clicking increase or decrease button or entering in the number of movies you would like to purchase.
3.	Remove any movies you do not consider buying anymore by clicking the remove button.
4.	If you’re ready to pay, click “Proceed to Checkout”
 
 ![image](https://user-images.githubusercontent.com/68848170/118177499-9eebc180-b3e7-11eb-8186-b395c8574793.png)


•	Check Out
1.	Click on the “$” button to purchase the items in the shopping cart.
2.	Fill in the form by entering your first name, last name, credit card number, and expiration date.
3.	Click on the reCAPTCHA button to ensure our website that you are not a bot.
4.	Click submit.
5.	You have finally purchased the movies of your choices.
 
![image](https://user-images.githubusercontent.com/68848170/118177507-a14e1b80-b3e7-11eb-9dd3-999b8f5a0e46.png)
