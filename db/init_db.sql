CREATE TABLE Account (
           id bigserial PRIMARY KEY,
           label text,
           code text UNIQUE,
           category text,
           amount numeric(24,3),
           open_date date
);

INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account 47426', '47426', 'personal', 111.222, '2023-01-01');
INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account 99999', '99999', 'offbalance', -1000, '2022-04-01');
INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account 47411', '47411', 'personal', 100000, '2020-04-05');
INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account 99998', '99998', 'offbalance', -1.5, '2021-03-07');
INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account 20202', '20202', 'personal', 222.3, '2022-12-12');
INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account 70601', '70601', 'personal', 777, '2021-07-07');
INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account 93601', '93601', 'derivative', -100, '2022-06-06');
INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account 53231', '53231', 'fictious', 123.456, '2021-05-10');
INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account Test', '63102', 'fictious', 1000001, '2021-11-28');
INSERT INTO Account(label, code, category, amount, open_date)
            VALUES ('Account Test', '40702', 'personal', -1000001, '2023-09-01');