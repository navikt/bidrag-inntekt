-- Table: overgangsstonad

-- DROP TABLE overgangsstonad

CREATE TABLE IF NOT EXISTS overgangsstonad
(
    overgangsstonad_id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    grunnlagspakke_id integer NOT NULL,
    part_person_id varchar(50) not null,
    periode_fra date NOT NULL,
    periode_til date,
    aktiv boolean DEFAULT true NOT NULL,
    bruk_fra timestamp DEFAULT now() NOT NULL,
    bruk_til timestamp,
    belop integer NOT NULL,
    hentet_tidspunkt timestamp DEFAULT now() NOT NULL,
    CONSTRAINT overgangsstonad_pkey PRIMARY KEY (overgangsstonad_id),
    CONSTRAINT grunnlagspakke_overgangsstonad_fkey FOREIGN KEY (grunnlagspakke_id)
        REFERENCES grunnlagspakke (grunnlagspakke_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    UNIQUE (overgangsstonad_id, grunnlagspakke_id)
)
    TABLESPACE pg_default;

CREATE INDEX idx_overgangsstonad_1 ON overgangsstonad(grunnlagspakke_id, aktiv);