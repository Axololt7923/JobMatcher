CREATE TABLE companies
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    website     VARCHAR(255),
    created_at  TIMESTAMP        DEFAULT NOW(),
    contact_email VARCHAR(255) NOT NULL
);

CREATE TABLE users
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    full_name  VARCHAR(255),
    created_at TIMESTAMP        DEFAULT NOW(),
    updated_at TIMESTAMP        DEFAULT NOW(),
    company_id UUID REFERENCES companies (id)
);

CREATE TABLE cvs
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    file_url   VARCHAR(500),
    chroma_id  VARCHAR(255) UNIQUE,
    is_active  BOOLEAN          DEFAULT TRUE,
    created_at TIMESTAMP        DEFAULT NOW()
);

CREATE TABLE cv_parsed_data
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cv_id            UUID NOT NULL REFERENCES cvs (id) ON DELETE CASCADE,
    skills           TEXT[],      -- ['Python', 'Java', 'Spring Boot']
    experience_years FLOAT,
    education_level  VARCHAR(50), -- 'bachelor', 'master'...
    languages        TEXT[],      -- ['Vietnamese', 'English']
    summary          TEXT,
    raw_json         JSONB,
    created_at       TIMESTAMP        DEFAULT NOW()
);

CREATE TABLE jobs
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id   UUID         REFERENCES companies (id) ON DELETE SET NULL,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    requirements TEXT,
    salary_min   INTEGER,
    salary_max   INTEGER,
    location     VARCHAR(255),
    job_type     VARCHAR(50) CHECK (job_type IN ('full_time', 'part_time')),
    chroma_id    VARCHAR(255) UNIQUE,
    source_url   VARCHAR(500),
    created_by   UUID REFERENCES users (id),
    is_active    BOOLEAN          DEFAULT TRUE,
    created_at   TIMESTAMP        DEFAULT NOW(),
    expired_at   TIMESTAMP
);


CREATE TABLE applications
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    job_id     UUID NOT NULL REFERENCES jobs (id) ON DELETE CASCADE,
    cv_id      UUID NOT NULL REFERENCES cvs (id),
    status     VARCHAR(50)      DEFAULT 'applied'
        CHECK (status IN ('applied', 'viewed', 'interviewing', 'rejected', 'accepted')),
    applied_at TIMESTAMP        DEFAULT NOW(),
    updated_at TIMESTAMP        DEFAULT NOW(),
    UNIQUE (user_id, job_id)
);

CREATE INDEX idx_cvs_user_id ON cvs (user_id);
CREATE INDEX idx_jobs_company_id ON jobs (company_id);
CREATE INDEX idx_jobs_is_active ON jobs (is_active);
CREATE INDEX idx_applications_user_id ON applications (user_id);
CREATE INDEX idx_applications_job_id ON applications (job_id);