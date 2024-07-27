package com.thuverx.resource.model;

import com.thuverx.resource.structure.models.GeometryJSON;

public record Geometry(GeometryJSON.Geometry json, String identifier) {}
