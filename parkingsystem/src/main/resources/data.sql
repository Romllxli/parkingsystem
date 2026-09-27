INSERT INTO parking_spots (spot_number, allowed_type, is_occupied)
VALUES ('A-01', 'CAR', false)
    ON CONFLICT (spot_number) DO NOTHING;

INSERT INTO parking_spots (spot_number, allowed_type, is_occupied)
VALUES ('A-02', 'CAR', false)
    ON CONFLICT (spot_number) DO NOTHING;

INSERT INTO parking_spots (spot_number, allowed_type, is_occupied)
VALUES ('A-03', 'CAR', false)
    ON CONFLICT (spot_number) DO NOTHING;

INSERT INTO parking_spots (spot_number, allowed_type, is_occupied)
VALUES ('B-01', 'TRUCK', false)
    ON CONFLICT (spot_number) DO NOTHING;

INSERT INTO parking_spots (spot_number, allowed_type, is_occupied)
VALUES ('B-02', 'TRUCK', false)
    ON CONFLICT (spot_number) DO NOTHING;