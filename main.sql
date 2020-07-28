DROP PROCEDURE IF EXISTS main;

DELIMITER $$
CREATE PROCEDURE main
(IN movieTitle varchar(100), IN movieYear int(11), IN movieDir varchar(100))
BEGIN
	DECLARE movieID varchar(10);
    
    SELECT RIGHT(CONCAT('0000000', CONVERT((CONVERT(SUBSTRING(MAX(id), 3, 7), unsigned) + 1), char)), 7) INTO movieID FROM movies;
    SET movieID = CONCAT('tt', movieID);
        
    -- If movie does not exist...
    IF NOT EXISTS (SELECT * FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDir) THEN	
        INSERT INTO movies VALUES(movieID, movieTitle, movieYear, movieDir);
        INSERT INTO ratings VALUES(movieID, 0.0, 0);
	END IF;	
END
$$