DROP PROCEDURE IF EXISTS add_movie;

DELIMITER $$
CREATE PROCEDURE add_movie
(IN movieTitle varchar(100), IN movieYear int(11), IN movieDir varchar(100),
IN star varchar(100), IN genre varchar(32), OUT message varchar(100))
BEGIN
	DECLARE movieID varchar(10);
    DECLARE starID varchar(10);
    
    SELECT RIGHT(CONCAT('0000000', CONVERT((CONVERT(SUBSTRING(MAX(id), 3, 7), unsigned) + 1), char)), 7) INTO movieID FROM movies;
    SET movieID = CONCAT('tt', movieID);
    
    IF NOT EXISTS (SELECT * FROM stars WHERE name = star) THEN
		SELECT CONCAT('nm', CONVERT((CONVERT(SUBSTRING(MAX(id), 3, 7), unsigned) + 1), char)) INTO starID FROM stars;
	ELSE
		SELECT id INTO starID FROM stars WHERE name = star LIMIT 1;
    END IF;
    
    -- If movie does not exist...
    IF NOT EXISTS (SELECT * FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDir) THEN	
        INSERT INTO movies VALUES(movieID, movieTitle, movieYear, movieDir);
		INSERT INTO stars(id, name)
		SELECT * FROM (SELECT starID, star) AS inputStars WHERE NOT EXISTS (SELECT * FROM stars WHERE name = star);
		INSERT INTO stars_in_movies VALUES(starID, movieID);
		INSERT INTO genres (name)
		SELECT * FROM (SELECT genre) AS inputGenres WHERE NOT EXISTS (SELECT * FROM genres WHERE name = genre);
		INSERT INTO genres_in_movies VALUES((SELECT id FROM genres WHERE name = genre), movieID);
        INSERT INTO ratings VALUES(movieID, 0.0, 0);
        
        SET message = 'Movie added successfully';
	ELSE
		SET message = 'Cannot add an existing movie (same title, director, year)';
	END IF;	
END
$$