package org.smartregister.plugin.gradle;

import android.support.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Vincent Karuri on 09/03/2020
 */
public class JsonFormUtils {
	// Helper functions

	/**
	 * Returns a JSONArray of all the forms fields in a single step form.
	 *
	 * @param jsonForm {@link JSONObject}
	 * @return fields {@link JSONArray}
	 */
	public static JSONArray getSingleStepFormfields(JSONObject jsonForm) {
		JSONObject step1 = jsonForm.optJSONObject(STEP1);
		if (step1 == null) {
			return null;
		}
		return step1.optJSONArray(FIELDS);
	}

	/**
	 * Refactored for backward compatibility invokes getMultiStepFormFields which provides the same result
	 * Returns a JSONArray of all the forms fields in a single or multi step form.
	 *
	 * @param jsonForm {@link JSONObject}
	 * @return fields {@link JSONArray}
	 */
	public static JSONArray fields(JSONObject jsonForm) {

		return getMultiStepFormFields(jsonForm);

	}

	/**
	 * Returns a JSONArray of all the forms fields in a multi step form.
	 *
	 * @param jsonForm {@link JSONObject}
	 * @return fields {@link JSONArray}
	 * @author dubdabasoduba
	 */
	public static JSONArray getMultiStepFormFields(JSONObject jsonForm) {
		JSONArray fields = new JSONArray();
		try {
			int stepCount = Integer.parseInt(jsonForm.optString(AllConstants.COUNT, "1"));

			if (stepCount == 1) {

				return getSingleStepFormfields(jsonForm);

			} else {

				for (int i = 0; i < stepCount; i++) {
					String stepName = AllConstants.STEP + (i + 1);
					JSONObject step = jsonForm.has(stepName) ? jsonForm.getJSONObject(stepName) : null;
					if (step != null && step.has(FIELDS)) {
						JSONArray stepFields = step.getJSONArray(FIELDS);
						for (int k = 0; k < stepFields.length(); k++) {
							JSONObject field = stepFields.getJSONObject(k);
							fields.put(field);
						}
					}
				}
			}

		} catch (JSONException e) {
			Timber.e(e);
		}
		return fields;
	}

	/**
	 * return field values that are in sections e.g for the hia2 monthly draft form which has sections
	 *
	 * @param jsonForm
	 * @return
	 */
	public static Map<String, String> sectionFields(JSONObject jsonForm) {
		try {

			JSONObject step1 = jsonForm.has(STEP1) ? jsonForm.getJSONObject(STEP1) : null;
			if (step1 == null) {
				return null;
			}

			JSONArray sections = step1.has(SECTIONS) ? step1.getJSONArray(SECTIONS) : null;
			if (sections == null) {
				return null;
			}

			Map<String, String> result = new HashMap<>();
			for (int i = 0; i < sections.length(); i++) {
				JSONObject sectionsJSONObject = sections.getJSONObject(i);
				if (sectionsJSONObject.has(FIELDS)) {
					JSONArray fieldsArray = sectionsJSONObject.getJSONArray(FIELDS);
					for (int j = 0; j < fieldsArray.length(); j++) {
						JSONObject fieldJsonObject = fieldsArray.getJSONObject(j);
						String key = fieldJsonObject.getString(KEY);
						String value = fieldJsonObject.getString(VALUE);
						result.put(key, value);

					}
				}

			}
			return result;

		} catch (JSONException e) {
			Timber.e(e);
			return null;
		}

	}

	public static JSONObject toJSONObject(String jsonString) {
		JSONObject jsonObject = null;
		try {
			jsonObject = jsonString == null ? null : new JSONObject(jsonString);
		} catch (JSONException e) {
			Timber.e(e);
		}
		return jsonObject;
	}

	public static String getFieldValue(JSONArray jsonArray, FormEntityConstants.Person person) {
		if (isBlankJsonArray(jsonArray)) {
			return null;
		}

		if (person == null) {
			return null;
		}

		return value(jsonArray, person.entity(), person.entityId());
	}

	public static String getFieldValue(JSONArray jsonArray, FormEntityConstants.Encounter encounter) {
		if (isBlankJsonArray(jsonArray)) {
			return null;
		}

		if (encounter == null) {
			return null;
		}

		return value(jsonArray, encounter.entity(), encounter.entityId());
	}

	public static String getFieldValue(String jsonString, String key) {
		JSONObject jsonForm = toJSONObject(jsonString);
		if (jsonForm == null) {
			return null;
		}

		JSONArray fields = fields(jsonForm);
		if (fields == null) {
			return null;
		}

		return getFieldValue(fields, key);

	}

	@Nullable
	public static JSONObject getFieldJSONObject(JSONArray jsonArray, String key) {
		JSONObject jsonObject = null;
		if (isBlankJsonArray(jsonArray)) {
			return jsonObject;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject currJsonObject = getJSONObject(jsonArray, i);
			String keyVal = getString(currJsonObject, KEY);
			if (keyVal != null && keyVal.equals(key)) {
				jsonObject = currJsonObject;
				break;
			}
		}

		return jsonObject;
	}

	@Nullable
	public static String value(JSONArray jsonArray, String entity, String entityId) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = getJSONObject(jsonArray, i);
			if (StringUtils.isNotBlank(getString(jsonObject, ENTITY_ID))) {
				continue;
			}
			String entityVal = getString(jsonObject, OPENMRS_ENTITY);
			String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
			if (entityVal != null && entityVal.equals(entity) && entityIdVal != null && entityIdVal.equals(entityId)) {
				return getString(jsonObject, VALUE);
			}

		}
		return null;
	}

	@Nullable
	public static String getFieldValue(JSONArray jsonArray, String key) {
		if (isBlankJsonArray(jsonArray)) {
			return null;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = getJSONObject(jsonArray, i);
			String keyVal = getString(jsonObject, KEY);
			if (keyVal != null && keyVal.equals(key)) {
				return getString(jsonObject, VALUE);
			}
		}
		return null;
	}

	@Nullable
	public static JSONObject getJSONObject(JSONArray jsonArray, int index) {
		return isBlankJsonArray(jsonArray) ? null : jsonArray.optJSONObject(index);
	}

	@Nullable
	public static JSONArray getJSONArray(JSONObject jsonObject, String field) {
		return isBlankJsonObject(jsonObject) ? null : jsonObject.optJSONArray(field);
	}

	public static JSONObject getJSONObject(JSONObject jsonObject, String field) {
		return isBlankJsonObject(jsonObject) ? null : jsonObject.optJSONObject(field);
	}

	public static String getString(JSONObject jsonObject, String field) {
		if (jsonObject == null) {
			return null;
		}

		try {
			return jsonObject.has(field) ? jsonObject.getString(field) : null;
		} catch (JSONException e) {
			return null;

		}
	}

	public static String getString(String jsonString, String field) {
		return getString(toJSONObject(jsonString), field);
	}

	public static Long getLong(JSONObject jsonObject, String field) {
		if (jsonObject == null) {
			return null;
		}

		try {
			return jsonObject.has(field) ? jsonObject.getLong(field) : null;
		} catch (JSONException e) {
			return null;

		}
	}

	public static void addToJSONObject(JSONObject jsonObject, String key, String value) {
		try {
			if (jsonObject == null) {
				return;
			}

			jsonObject.put(key, value);
		} catch (JSONException e) {
			Timber.e(e);
		}
	}

	public static String[] getNames(JSONObject jo) {
		int length = jo.length();
		if (length == 0) {
			return null;
		}
		Iterator i = jo.keys();
		String[] names = new String[length];
		int j = 0;
		while (i.hasNext()) {
			names[j] = (String) i.next();
			j += 1;
		}
		return names;
	}

	public static String convertToOpenMRSDate(String value) {

	private static boolean isBlankJsonArray(JSONArray jsonArray) {
		return jsonArray == null || jsonArray.length() == 0;
	}

	private static boolean isBlankJsonObject(JSONObject jsonObject) {
		return jsonObject == null || jsonObject.length() == 0;
	}
}
