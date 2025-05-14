DELIMITER $$
DROP PROCEDURE IF EXISTS pay_my_buddy.update_user;

CREATE PROCEDURE pay_my_buddy.update_user ( IN user_id INT,
											IN user_username VARCHAR(45),
                                            IN user_email VARCHAR(150),
                                            IN user_password VARCHAR(250),
                                            IN user_solde DOUBLE,
                                            OUT result INT)
BEGIN
	DECLARE current_username VARCHAR(45);
	DECLARE current_email VARCHAR(150);
    DECLARE current_password VARCHAR(250);
    DECLARE current_solde DOUBLE;
    DECLARE current_id INT;

    -- Gestion erreur
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			SET result = 0;
			ROLLBACK;
		END;
    START TRANSACTION;

    -- Récupération des valeurs actuelles
    SELECT id, username, email, `password`, solde
    INTO current_id, current_username, current_email, current_password, current_solde
    FROM `user` WHERE id = user_id;


    -- ajout de la nouvelle valeur si différente de null et de la valeur initiale
    IF current_id IS NOT NULL THEN
		IF user_username IS NOT NULL AND user_username <> current_username THEN
				UPDATE `user` SET username = user_username WHERE id = user_id;
		END IF;
        IF user_email IS NOT NULL AND user_email <> current_email THEN
			UPDATE `user` SET email = user_email WHERE id = user_id;
		END IF;
		IF user_password IS NOT NULL AND user_password <> current_password THEN
			UPDATE `user` SET password = user_password WHERE id = user_id;
		END IF;
		IF user_solde IS NOT NULL AND user_solde <> current_solde THEN
			UPDATE `user` SET solde = user_solde WHERE id = user_id;
		END IF;
			SET result = 1; -- success
	END IF;

    COMMIT;
END $$

DELIMITER ;