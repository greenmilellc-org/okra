/*
 *  Copyright (c) 2016 Fernando Nogueira
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package okra.builder;

import okra.base.AbstractOkra;
import okra.base.OkraItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class OkraBuilder<T extends OkraItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OkraBuilder.class);

    private String collection;
    private String database;
    private Class<T> scheduleItemClass;
    private Long expireDuration;
    private TimeUnit expireDurationUnit;

    public OkraBuilder<T> withScheduledItemClass(Class<T> scheduledItemClass) {
        this.scheduleItemClass = scheduledItemClass;
        return this;
    }

    public OkraBuilder<T> withSchedulerCollectionName(String collectionName) {
        this.collection = collectionName;
        return this;
    }

    public OkraBuilder<T> withDatabase(String database) {
        this.database = database;
        return this;
    }

    public OkraBuilder<T> withExpiration(long duration, TimeUnit durationUnit) {
        this.expireDuration = duration;
        this.expireDurationUnit = durationUnit;
        return this;
    }

    public abstract AbstractOkra<T> build();

    abstract void validateConfiguration();

    public String getCollection() {
        return collection;
    }

    public String getDatabase() {
        return database;
    }

    public Class<T> getScheduleItemClass() {
        return scheduleItemClass;
    }

    public Long getExpireDuration() {
        return expireDuration;
    }

    public TimeUnit getExpireDurationUnit() {
        return expireDurationUnit;
    }
}
