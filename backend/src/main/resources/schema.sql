-- ModelRouter PostgreSQL Schema DDL & Seed Data

CREATE TABLE IF NOT EXISTS organizations (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    plan VARCHAR(50) DEFAULT 'FREE',
    budget_limit NUMERIC(10, 4),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS api_keys (
    id VARCHAR(36) PRIMARY KEY,
    organization_id VARCHAR(36) REFERENCES organizations(id),
    key_hash VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS providers (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    base_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS models (
    id VARCHAR(36) PRIMARY KEY,
    provider_id VARCHAR(36) REFERENCES providers(id),
    name VARCHAR(100) NOT NULL,
    capabilities VARCHAR(255),
    context_limit INT NOT NULL,
    input_price_per_1k NUMERIC(10, 6) NOT NULL,
    output_price_per_1k NUMERIC(10, 6) NOT NULL,
    quality_score NUMERIC(3, 2) DEFAULT 0.80,
    latency_score NUMERIC(3, 2) DEFAULT 0.80,
    reliability_score NUMERIC(3, 2) DEFAULT 0.99,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS routing_requests (
    id VARCHAR(36) PRIMARY KEY,
    organization_id VARCHAR(36) REFERENCES organizations(id),
    request_id VARCHAR(100) UNIQUE NOT NULL,
    selected_model_id VARCHAR(36) REFERENCES models(id),
    status VARCHAR(20) NOT NULL,
    mode VARCHAR(50) NOT NULL,
    latency_ms INT NOT NULL,
    input_tokens INT NOT NULL,
    output_tokens INT NOT NULL,
    estimated_cost NUMERIC(10, 6) NOT NULL,
    cache_hit BOOLEAN DEFAULT FALSE,
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Seed Default Organization & Admin Key (Hash of 'mr_key_demo_12345')
INSERT INTO organizations (id, name, plan, budget_limit)
VALUES ('org-demo-001', 'Acme Corp AI Team', 'ENTERPRISE', 1000.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO api_keys (id, organization_id, key_hash, name, status)
VALUES ('key-demo-001', 'org-demo-001', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'Demo API Key', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Seed Default Providers
INSERT INTO providers (id, name, base_url, status)
VALUES 
('prov-openai', 'OpenAI', 'https://api.openai.com/v1', 'ACTIVE'),
('prov-anthropic', 'Anthropic', 'https://api.anthropic.com/v1', 'ACTIVE'),
('prov-mock', 'Mock Provider', 'https://mock.modelrouter.internal', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Seed Default Models Catalog
INSERT INTO models (id, provider_id, name, capabilities, context_limit, input_price_per_1k, output_price_per_1k, quality_score, latency_score, reliability_score, status)
VALUES
('model-gpt-4o', 'prov-openai', 'gpt-4o', 'chat,code,vision,long-context', 128000, 0.0025, 0.0100, 0.95, 0.70, 0.99, 'ACTIVE'),
('model-gpt-4o-mini', 'prov-openai', 'gpt-4o-mini', 'chat,code', 128000, 0.00015, 0.0006, 0.82, 0.92, 0.99, 'ACTIVE'),
('model-claude-3-5-sonnet', 'prov-anthropic', 'claude-3-5-sonnet-20240620', 'chat,code,reasoning,long-context', 200000, 0.0030, 0.0150, 0.96, 0.65, 0.98, 'ACTIVE'),
('model-mock-cheap', 'prov-mock', 'mock-cheap-v1', 'chat,code', 32000, 0.00010, 0.0002, 0.65, 0.95, 0.99, 'ACTIVE'),
('model-mock-fast', 'prov-mock', 'mock-fast-v1', 'chat', 16000, 0.00020, 0.0004, 0.75, 0.98, 0.99, 'ACTIVE')
ON CONFLICT (id) DO NOTHING;
