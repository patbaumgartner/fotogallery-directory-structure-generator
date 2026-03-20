package com.fortytwotalents.fotogallery.model;

import java.util.List;

public record CsvReadResult(List<GalleryEntry> entries, String eventName) {
}
