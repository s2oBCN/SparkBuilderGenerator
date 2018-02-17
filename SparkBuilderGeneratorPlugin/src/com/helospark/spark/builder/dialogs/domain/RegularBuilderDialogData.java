package com.helospark.spark.builder.dialogs.domain;

import java.util.Collections;
import java.util.List;

import javax.annotation.Generated;

public class RegularBuilderDialogData {
    private List<RegularBuilderFieldIncludeFieldIncludeDomain> regularBuilderFieldIncludeFieldIncludeDomains;
    private boolean shouldCreateCopyMethod;

    public List<RegularBuilderFieldIncludeFieldIncludeDomain> getRegularBuilderFieldIncludeFieldIncludeDomains() {
        return regularBuilderFieldIncludeFieldIncludeDomains;
    }

    public boolean isShouldCreateCopyMethod() {
        return shouldCreateCopyMethod;
    }

    @Generated("SparkTools")
    private RegularBuilderDialogData(Builder builder) {
        this.regularBuilderFieldIncludeFieldIncludeDomains = builder.regularBuilderFieldIncludeFieldIncludeDomains;
        this.shouldCreateCopyMethod = builder.shouldCreateCopyMethod;
    }

    @Generated("SparkTools")
    public static Builder builder() {
        return new Builder();
    }

    @Generated("SparkTools")
    public static final class Builder {
        private List<RegularBuilderFieldIncludeFieldIncludeDomain> regularBuilderFieldIncludeFieldIncludeDomains = Collections.emptyList();
        private boolean shouldCreateCopyMethod;

        private Builder() {
        }

        public Builder withRegularBuilderFieldIncludeFieldIncludeDomains(List<RegularBuilderFieldIncludeFieldIncludeDomain> regularBuilderFieldIncludeFieldIncludeDomains) {
            this.regularBuilderFieldIncludeFieldIncludeDomains = regularBuilderFieldIncludeFieldIncludeDomains;
            return this;
        }

        public Builder withShouldCreateCopyMethod(boolean shouldCreateCopyMethod) {
            this.shouldCreateCopyMethod = shouldCreateCopyMethod;
            return this;
        }

        public RegularBuilderDialogData build() {
            return new RegularBuilderDialogData(this);
        }
    }

}
