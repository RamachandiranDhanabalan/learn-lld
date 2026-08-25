package com.learn.lld;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Day 12 Practice Problem — Analytics Dashboard
 *
 * Problem: AnalyticsDashboard was a broken Singleton, had multiple responsibilities
 * (load + render + export), public fields, fat interface (LSP/ISP violation),
 * if-else on source type, long param list, and third-party SDKs with incompatible APIs.
 *
 * Concepts tested: Adapter, Strategy, Factory, SRP, OCP, LSP, ISP, DIP,
 * Singleton issues, Encapsulation, Builder/Value Object
 *
 * Key Learning: Adapter vs Strategy vs Factory — same structure (interface + impls),
 * different INTENT:
 *   - Adapter = wraps incompatible foreign class, TRANSLATES
 *   - Strategy = your own interchangeable algorithms
 *   - Factory = picks which implementation to use
 */

// ══════════ DOMAIN ══════════

class DataPoint {
    private final String metric;
    private final double value;
    private final String timestamp;

    DataPoint(String metric, double value, String timestamp) {
        this.metric = metric;
        this.value = value;
        this.timestamp = timestamp;
    }

    String getMetric() { return metric; }
    double getValue() { return value; }
    String getTimestamp() { return timestamp; }
}

// Value object — replaces long param list
class AnalyticsRequest {
    private final String dateRange;
    private final int maxRecords;
    private final String timezone;
    private final boolean includeArchived;

    AnalyticsRequest(String dateRange, int maxRecords, String timezone, boolean includeArchived) {
        this.dateRange = dateRange;
        this.maxRecords = maxRecords;
        this.timezone = timezone;
        this.includeArchived = includeArchived;
    }

    String getDateRange() { return dateRange; }
    int getMaxRecords() { return maxRecords; }
    String getTimezone() { return timezone; }
    boolean isIncludeArchived() { return includeArchived; }
}

// ══════════ ADAPTER — wraps third-party SDKs behind our interface ══════════

// TARGET interface (what our system uses)
interface AnalyticsDataSource {
    List<DataPoint> fetchData(AnalyticsRequest request);
}

// ADAPTER 1 — translates Google Analytics SDK → our interface
class GoogleAnalyticsAdapter implements AnalyticsDataSource {
    private final GoogleAnalyticsSDK ga;  // third-party, can't modify

    GoogleAnalyticsAdapter(String apiKey) {
        this.ga = new GoogleAnalyticsSDK(apiKey);
    }

    @Override
    public List<DataPoint> fetchData(AnalyticsRequest request) {
        // Call THEIR API
        GAResponse response = ga.fetchReport(request.getDateRange(), request.getMaxRecords());
        // TRANSLATE their response → our DataPoint
        return response.getRows().stream()
            .map(row -> new DataPoint(row.getMetricName(), row.getValue(), row.getDate()))
            .collect(Collectors.toList());
    }
}

// ADAPTER 2 — translates Mixpanel SDK → our interface
class MixpanelAdapter implements AnalyticsDataSource {
    private final MixpanelClient mp;  // third-party, different API entirely

    MixpanelAdapter(String apiKey) {
        this.mp = new MixpanelClient(apiKey, "US");
    }

    @Override
    public List<DataPoint> fetchData(AnalyticsRequest request) {
        JSONArray events = mp.exportEvents(request.getDateRange(), request.getTimezone());
        List<DataPoint> points = new ArrayList<>();
        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);
            points.add(new DataPoint(
                event.getString("event_name"),
                event.getDouble("count"),
                event.getString("time")
            ));
        }
        return points;
    }
}

// ADAPTER 3 — translates Amplitude SDK → our interface
class AmplitudeAdapter implements AnalyticsDataSource {
    private final AmplitudeAPI amp;  // third-party, yet another different API

    AmplitudeAdapter(String apiKey) {
        this.amp = new AmplitudeAPI();
        this.amp.authenticate(apiKey);
    }

    @Override
    public List<DataPoint> fetchData(AnalyticsRequest request) {
        amp.setDateRange(request.getDateRange());
        amp.setIncludeArchived(request.isIncludeArchived());
        List<AmplitudeEvent> events = amp.query();
        return events.stream()
            .map(e -> new DataPoint(e.eventType, e.totalCount, e.eventDate))
            .collect(Collectors.toList());
    }
}

// ══════════ FACTORY — picks the right adapter ══════════

class AnalyticsSourceFactory {
    static AnalyticsDataSource create(String source, String apiKey) {
        return switch (source) {
            case "GOOGLE_ANALYTICS" -> new GoogleAnalyticsAdapter(apiKey);
            case "MIXPANEL" -> new MixpanelAdapter(apiKey);
            case "AMPLITUDE" -> new AmplitudeAdapter(apiKey);
            default -> throw new IllegalArgumentException("Unknown source: " + source);
        };
    }
}

// ══════════ STRATEGY — chart rendering (varies by type) ══════════

interface ChartRenderer {
    void render(List<DataPoint> data);
}

class BarChartRenderer implements ChartRenderer {
    public void render(List<DataPoint> data) {
        System.out.println("Rendering bar chart with " + data.size() + " points");
    }
}

class LineChartRenderer implements ChartRenderer {
    public void render(List<DataPoint> data) {
        System.out.println("Rendering line chart with " + data.size() + " points");
    }
}

class PieChartRenderer implements ChartRenderer {
    public void render(List<DataPoint> data) {
        System.out.println("Rendering pie chart with " + data.size() + " points");
    }
}

// ══════════ STRATEGY — export format (varies by type) ══════════

interface DataExporter {
    String export(List<DataPoint> data);
}

class CsvExporter implements DataExporter {
    public String export(List<DataPoint> data) {
        StringBuilder sb = new StringBuilder("metric,value,timestamp\n");
        data.forEach(dp ->
            sb.append(dp.getMetric()).append(",")
              .append(dp.getValue()).append(",")
              .append(dp.getTimestamp()).append("\n"));
        return sb.toString();
    }
}

class JsonExporter implements DataExporter {
    public String export(List<DataPoint> data) {
        // In real code: new Gson().toJson(data)
        return "[" + data.stream()
            .map(dp -> "{\"metric\":\"" + dp.getMetric() + "\",\"value\":" + dp.getValue() + "}")
            .collect(Collectors.joining(",")) + "]";
    }
}

// ══════════ ISP — split the fat AnalyticsSource interface ══════════

interface Cacheable {
    void clearCache();
}

interface Schedulable {
    void scheduleRefresh(String cronExpression);
}

interface Configurable {
    void configure(Map<String, Object> settings);
}

// Each source implements only what it supports — no UnsupportedOperationException
// GoogleAnalyticsAdapter can also implement Cacheable, Configurable if needed

// ══════════ DASHBOARD SERVICE — orchestrator (SRP) ══════════

class AnalyticsDashboard {
    private final AnalyticsDataSource dataSource;  // Adapter (DIP)
    private final ChartRenderer renderer;          // Strategy (DIP)
    private final DataExporter exporter;           // Strategy (DIP)

    AnalyticsDashboard(AnalyticsDataSource dataSource, ChartRenderer renderer, DataExporter exporter) {
        this.dataSource = dataSource;
        this.renderer = renderer;
        this.exporter = exporter;
    }

    void loadAndRender(AnalyticsRequest request) {
        List<DataPoint> data = dataSource.fetchData(request);
        renderer.render(data);
    }

    String loadAndExport(AnalyticsRequest request) {
        List<DataPoint> data = dataSource.fetchData(request);
        return exporter.export(data);
    }
}

// ══════════ USAGE ══════════

class Day12AdapterPattern {
    public static void main(String[] args) {
        // Factory picks the right Adapter
        AnalyticsDataSource source = AnalyticsSourceFactory.create("GOOGLE_ANALYTICS", "api-key-123");

        // Strategies for rendering and export
        ChartRenderer chart = new BarChartRenderer();
        DataExporter export = new CsvExporter();

        // Dashboard orchestrates (DI via constructor)
        AnalyticsDashboard dashboard = new AnalyticsDashboard(source, chart, export);

        // Use
        AnalyticsRequest request = new AnalyticsRequest("2026-01-01:2026-08-25", 500, "IST", false);
        dashboard.loadAndRender(request);
        String csv = dashboard.loadAndExport(request);
        System.out.println(csv);
    }
}
