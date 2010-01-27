/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.googlecode.osde.internal.ui;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.osde.internal.OsdePreferencesModel;
import com.googlecode.osde.internal.utils.OpenSocialUtil;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

/**
 * @author chingyichan.tw@gmail.com (qrtt1)
 */
public class OsdePreferenceBinder {

    private Map<String, Object> store = new HashMap<String, Object>();
    private Map<String, Class<?>> types = new HashMap<String, Class<?>>();
    private DataBindingContext ctx = new DataBindingContext();
    private OsdePreferencesModel model;

    public OsdePreferenceBinder(OsdePreferencesModel model) {
        this.model = model;
    }

    public void bind(Control control, String preferenceName, Class<?> type) {
        bind(control, preferenceName, type, null, null);
    }

    public void bind(Control control, String preferenceName, Class<?> type,
            IConverter targetToModel, IConverter modelToTarget) {
        IObservableValue model = new ObservableMapValue(store, preferenceName);
        IObservableValue ui = null;

        UpdateValueStrategy modelUpdater = new UpdateValueStrategy();
        UpdateValueStrategy uiUpdater = new UpdateValueStrategy();

        if (targetToModel != null) {
            modelUpdater.setConverter(targetToModel);
        }

        if (modelToTarget != null) {
            uiUpdater.setConverter(modelToTarget);
        }

        if (control instanceof Button) {
            ui = SWTObservables.observeSelection(control);
        }

        if (ui == null) {
            ui = SWTObservables.observeText(control);
        }

        propagateData(preferenceName, type);
        ctx.bindValue(ui, model, modelUpdater, uiUpdater);
    }

    private void propagateData(String preferenceName, Class<?> type) {
        types.put(preferenceName, type);
        if (String.class.equals(type)) {
            store.put(preferenceName, model.get(preferenceName));
        } else {
            store.put(preferenceName, model.getBoolean(preferenceName));
        }
    }

    public void updateUI() {
        ctx.updateTargets();
    }

    public void store() {
        model.store(store);
    }

    public void updateDefaultUI() {
        for (String preferenceName : store.keySet()) {
            if (String.class.equals(types.get(preferenceName))) {
                store.put(preferenceName, model.getDefault(preferenceName));
            } else {
                store.put(preferenceName, model
                        .getDefaultBoolean(preferenceName));
            }
        }
        updateUI();
    }

    static class ObservableMapValue extends AbstractObservableValue {

        private String key;
        private Map<String, Object> data;

        public ObservableMapValue(Map<String, Object> data, String key) {
            this.data = data;
            this.key = key;

        }

        @Override
        protected Object doGetValue() {
            return data.get(key);
        }

        public Object getValueType() {
            return null;
        }

        @Override
        protected void doSetValue(Object value) {
            data.put(key, value);
        }
    }

    static abstract class ConverterAdapter implements IConverter {

        public abstract Object convert(Object fromObject);

        public Object getFromType() {
            return null;
        }

        public Object getToType() {
            return null;
        }
    }

    final static ConverterAdapter LOCALE_CONVERTER = new ConverterAdapter() {
        public Object convert(Object fromObject) {
            if (fromObject instanceof String) {
                String data = (String) fromObject;
                String value = StringUtils.substringBetween(data, "(", ")");

                if (value != null) {
                    return value;
                }

                // try languages
                String result = null;
                for (String lang : OpenSocialUtil.LANGUAGES) {
                    if (getValue(data, lang)) {
                        result = lang;
                        break;
                    }
                }
                if (result != null) {
                    return result;
                }

                // try countries
                for (String country : OpenSocialUtil.COUNTRIES) {
                    if (getValue(data, country)) {
                        result = country;
                        break;
                    }
                }
                if (result != null) {
                    return result;
                }

                return fromObject;
            }
            return null;
        }

        private boolean getValue(String data, String lang) {
            return StringUtils.equals(data, StringUtils.substringBetween(lang,
                    "(", ")"));
        }
    };
}
