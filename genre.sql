DROP PROCEDURE IF EXISTS genre;

DELIMITER $$
CREATE PROCEDURE genre
(IN movieTitle varchar(100), IN movieYear int(11), IN movieDir varchar(100), IN genre varchar(32))
BEGIN
	DECLARE movieID varchar(10);

    SELECT id INTO movieID FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDir;
    
	INSERT INTO genres (name)
	SELECT * FROM (SELECT genre) AS inputGenres WHERE NOT EXISTS (SELECT * FROM genres WHERE name = genre);
	INSERT INTO genres_in_movies VALUES((SELECT id FROM genres WHERE name = genre), movieID);
END
$$