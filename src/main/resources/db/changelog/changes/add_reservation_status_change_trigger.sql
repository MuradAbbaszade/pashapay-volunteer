CREATE TRIGGER check_end_time_trigger
    BEFORE INSERT ON reservations
    FOR EACH ROW
BEGIN
    IF NEW.status = 'WAITING_FOR_APPROVE' AND ADDTIME(NEW.end_time, '00:15:00') < CURTIME() THEN
        SET NEW.status = 'DECLINED';
END IF;
END;
