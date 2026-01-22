CREATE TABLE dice_jobs (
                           id VARCHAR(100) NOT NULL,
                           title VARCHAR(255),
                           summary TEXT,
                           company_name VARCHAR(255),
                           job_location VARCHAR(255),
                           details_page_url TEXT,
                           company_page_url TEXT,
                           salary VARCHAR(255),
                           employment_type VARCHAR(255),
                           workplace_types VARCHAR(255),
                           posted_date VARCHAR(255),
                           easy_apply BOOLEAN NOT NULL DEFAULT FALSE,
                           dice_id VARCHAR(255),
                           PRIMARY KEY (id)
);