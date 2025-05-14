DELIMITER $$
DROP PROCEDURE IF EXISTS pay_my_buddy.create_user_if_email_not_existing;

CREATE PROCEDURE pay_my_buddy.create_user_if_email_not_existing (IN user_username VARCHAR(45),
																 IN user_email VARCHAR(150),
                                                                 IN user_password VARCHAR(250),
                                                                 OUT result INT)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			SET @result = 0;
			ROLLBACK;
		END;

    START TRANSACTION;

      INSERT INTO user (username, email, `password`,solde) VALUES (user_username, user_email, user_password,0);
      SELECT ROW_COUNT() INTO result;
      IF result > 0
        THEN SET result = 1;
      END IF;

    COMMIT;
END $$
DELIMITER ;
